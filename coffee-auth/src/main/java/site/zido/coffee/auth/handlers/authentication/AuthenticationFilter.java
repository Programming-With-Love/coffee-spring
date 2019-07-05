package site.zido.coffee.auth.handlers.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;
import site.zido.coffee.auth.context.UserHolder;
import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.exceptions.AbstractAuthenticationException;
import site.zido.coffee.auth.exceptions.InternalAuthenticationException;
import site.zido.coffee.auth.handlers.LoginFailureHandler;
import site.zido.coffee.auth.handlers.LoginSuccessHandler;
import site.zido.coffee.auth.handlers.UserManager;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 认证过滤器
 *
 * @author zido
 */
public class AuthenticationFilter extends GenericFilterBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);
    /**
     * 默认的登陆方式限定为POST
     */
    private static final String REQUEST_METHOD = "POST";
    /**
     * 登陆处理器map，键为对应的url
     */
    private Map<String, AuthHandler> handlerMap;
    /**
     * 用于帮助查询url
     */
    private UrlPathHelper urlPathHelper;
    /**
     * 失败处理器
     */
    private LoginFailureHandler failureHandler;
    /**
     * 成功处理器
     */
    private LoginSuccessHandler successHandler;
    /**
     * 用户管理器
     */
    private UserManager userManager;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String currentUrl = getRequestPath(request);
        //查询是否是登陆接口,并获得相关的登陆处理器
        AuthHandler authHandler = handlerMap.get(currentUrl);
        if (authHandler != null && REQUEST_METHOD.equals(request.getMethod())) {
            LOGGER.debug("请求认证处理中");
            IUser authResult;
            try {
                authResult = authHandler.attemptAuthentication(request, response);
                if (authResult == null) {
                    return;
                }
            } catch (InternalAuthenticationException failed) {
                logger.error(
                        "An internal error occurred while trying to authenticate the user.",
                        failed);
                unsuccessfulAuthentication(request, response, failed);
                return;
            } catch (AbstractAuthenticationException failed) {
                unsuccessfulAuthentication(request, response, failed);
                return;
            }
            successfulAuthentication(request, response, chain, authResult);
        } else {
            chain.doFilter(request, response);
        }
    }

    private void successfulAuthentication(HttpServletRequest request,
                                          HttpServletResponse response,
                                          FilterChain chain,
                                          IUser authResult) throws IOException, ServletException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("认证成功，更新userHolder:" + authResult);
        }
        userManager.bindUser(request, authResult);
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    private void unsuccessfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            AbstractAuthenticationException failed) throws IOException, ServletException {
        UserHolder.clearContext();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("请求认证失败:" + failed.toString(), failed);
        }
        failureHandler.onAuthenticationFailure(request, response, failed);
    }

    private String getRequestPath(HttpServletRequest request) {
        if (this.urlPathHelper != null) {
            return this.urlPathHelper.getPathWithinApplication(request);
        }
        String url = request.getServletPath();

        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            url = StringUtils.hasLength(url) ? url + pathInfo : pathInfo;
        }

        return url;
    }

    @Autowired
    public void setAuthenticationFailureHandler(
            LoginFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "failureHandler cannot be null");
        this.failureHandler = failureHandler;
    }

    @Autowired
    public void setAuthenticationSuccessHandler(
            LoginSuccessHandler successHandler) {
        Assert.notNull(successHandler, "successHandler cannot be null");
        this.successHandler = successHandler;
    }

    public void setHandlerMap(Map<String, AuthHandler> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Autowired(required = false)
    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }

    public Map<String, AuthHandler> getHandlerMap() {
        return handlerMap;
    }

    public UrlPathHelper getUrlPathHelper() {
        return urlPathHelper;
    }

    public LoginFailureHandler getFailureHandler() {
        return failureHandler;
    }

    public LoginSuccessHandler getSuccessHandler() {
        return successHandler;
    }

    @Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }


    @Override
    protected void initFilterBean() throws ServletException {
        Assert.notNull(userManager, "user manager can't be null");
        Assert.notNull(failureHandler, "failure handler can't be null");
        Assert.notNull(successHandler, "success handler can't be null");
    }
}
