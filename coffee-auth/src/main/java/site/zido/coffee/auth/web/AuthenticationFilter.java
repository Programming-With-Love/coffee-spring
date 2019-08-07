package site.zido.coffee.auth.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;
import site.zido.coffee.auth.authentication.*;
import site.zido.coffee.auth.context.UserHolder;
import site.zido.coffee.auth.context.UserManager;
import site.zido.coffee.auth.user.IUser;
import site.zido.coffee.auth.user.annotations.AuthEntity;
import site.zido.coffee.auth.handlers.LoginFailureHandler;
import site.zido.coffee.auth.handlers.LoginSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static site.zido.coffee.auth.Constants.DEFAULT_LOGIN_URL;

/**
 * 认证过滤器
 *
 * @author zido
 */
public class AuthenticationFilter extends GenericFilterBean implements BeanFactoryAware, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final String ERROR_WHEN_MULTI = String.format("多用户实体时需要使用%s标记，" +
            "并提供不同的url以帮助识别登录用户", AuthEntity.class.getName());
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

    private BeanFactory beanFactory;

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
        //TODO update detail
        successHandler.onAuthenticationSuccess(request, response, null);
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

    public void setAuthenticationFailureHandler(
            LoginFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }

    public void setAuthenticationSuccessHandler(
            LoginSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    public void setHandlerMap(Map<String, AuthHandler> handlerMap) {
        this.handlerMap = handlerMap;
    }

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

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * 后置处理,自动扫描需要注册自动注入的用户认证类
     * <p>
     * 目前仅支持jpa管理下的user实体，来源可以是任何支持jpa的数据库
     */
    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() {
        //repository扫描
        Map<String, JpaRepositoryFactoryBean> jpaRepositoryFactoryBeanMap =
                BeanFactoryUtils.beansOfTypeIncludingAncestors((ListableBeanFactory) beanFactory, JpaRepositoryFactoryBean.class);
        Map<String, AuthHandler> map = new HashMap<>();
        //认证器查询,可以自行扩展认证器
        Map<String, Authenticator> authenticatorMap =
                BeanFactoryUtils.beansOfTypeIncludingAncestors((ListableBeanFactory) beanFactory, Authenticator.class);
        for (JpaRepositoryFactoryBean factoryBean : jpaRepositoryFactoryBeanMap.values()) {
            Class<?> javaType = factoryBean.getEntityInformation().getJavaType();
            //查询实现IUser接口的对象,自动注入用户认证
            if (IUser.class.isAssignableFrom(javaType)) {
                JpaRepository repository = (JpaRepository) factoryBean.getObject();
                AuthEntity annotation = AnnotationUtils.getAnnotation(javaType, AuthEntity.class);
                String url;
                if (annotation != null) {
                    url = annotation.url().trim();
                    if (!url.startsWith("/")) {
                        url = "/" + url;
                    }
                } else {
                    url = DEFAULT_LOGIN_URL;
                }
                if (map.get(url) != null) {
                    throw new IllegalArgumentException(ERROR_WHEN_MULTI);
                }
                List<Authenticator> results = new ArrayList<>();
                authenticatorMap.forEach((s, authenticator) -> {
                    if (authenticator.prepare((Class<? extends IUser>) javaType, repository)) {
                        results.add(authenticator);
                    }
                });
                map.put(url, new SimpleAuthHandler(results));
            }
        }
        if (map.isEmpty()) {
            //TODO don't register filter
            return;
        }
        map = Collections.unmodifiableMap(map);
        this.setHandlerMap(map);
    }

    @Override
    protected void initFilterBean() throws ServletException {
        Assert.notNull(userManager, "user manager can't be null");
        Assert.notNull(failureHandler, "failure handler can't be null");
        Assert.notNull(successHandler, "success handler can't be null");
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
