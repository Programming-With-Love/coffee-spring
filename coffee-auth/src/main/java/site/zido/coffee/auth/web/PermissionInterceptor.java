package site.zido.coffee.auth.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import site.zido.coffee.auth.authentication.Auth;
import site.zido.coffee.auth.authentication.AuthVal;
import site.zido.coffee.auth.context.UserManager;
import site.zido.coffee.auth.core.Authentication;
import site.zido.coffee.auth.handlers.DisabledUserHandler;
import site.zido.coffee.auth.handlers.LoginExpectedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 权限判定拦截器
 *
 * @author zido
 */
public class PermissionInterceptor implements HandlerInterceptor, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionInterceptor.class);
    /**
     * 权限信息缓存
     */
    private static Map<HandlerMethod, AuthVal> authCache = new ConcurrentHashMap<>();
    /**
     * 未登录处理器
     */
    private LoginExpectedHandler loginExpectedHandler;
    /**
     * 用户被禁用处理器
     */
    private DisabledUserHandler disabledUserHandler;
    /**
     * 用户管理器
     */
    private UserManager userManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod method = (HandlerMethod) handler;
        AuthVal authVal = determinerAuth(method);
        if (authVal.isSkip()) {
            return true;
        }
        Collection<String> requiredRoles = authVal.getRoles();
        Authentication currentUser = userManager.getCurrentUser(request);
        if (currentUser == null) {
            loginExpectedHandler.handle(request, response);
            return false;
        }
        if (!currentUser.isAuthenticated()) {
            LOGGER.debug("current user is disabled");
            disabledUserHandler.handle(request, response);
            return false;
        }
        if (requiredRoles != null) {
            Collection<String> roles = userManager.getRoles(currentUser);
            if (noPermissions(requiredRoles, roles)) {
                loginExpectedHandler.handle(request, response);
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        userManager.clear();
    }

    /**
     * 方法鉴权信息读取
     *
     * @param handlerMethod 请求方法
     * @return auth val
     */
    private AuthVal determinerAuth(HandlerMethod handlerMethod) {
        return authCache.computeIfAbsent(handlerMethod, (el) -> {
            Auth methodAuth = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), Auth.class);
            Auth classAuth = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), Auth.class);
            AuthVal val = new AuthVal();
            if (methodAuth != null) {
                val.setSkip(false);
                handleAuth(val, methodAuth);
            }
            if (classAuth != null) {
                val.setSkip(false);
                handleAuth(val, classAuth);
            }
            return val;
        });
    }

    private void handleAuth(AuthVal val, Auth authAnnotation) {
        for (String role : authAnnotation.role()) {
            if (!"".equals(role) && null != role) {
                if (val.getRoles() == null) {
                    val.setRoles(new HashSet<>());
                }
                val.getRoles().add(role);
            }
        }
    }

    private boolean noPermissions(Collection<String> needs, Collection<String> requires) {
        Set<String> all = new HashSet<>(requires);
        all.retainAll(needs);
        return all.isEmpty();
    }

    @Autowired
    public void setLoginExpectedHandler(LoginExpectedHandler loginExpectedHandler) {
        this.loginExpectedHandler = loginExpectedHandler;
    }

    @Autowired
    public void setDisabledUserHandler(DisabledUserHandler disabledUserHandler) {
        this.disabledUserHandler = disabledUserHandler;
    }

    @Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(loginExpectedHandler, "loginExpectedHandler cannot be null");
        Assert.notNull(disabledUserHandler, "disabledUserHandler cannot be null");
        Assert.notNull(userManager, "userManager cannot be null");
    }
}
