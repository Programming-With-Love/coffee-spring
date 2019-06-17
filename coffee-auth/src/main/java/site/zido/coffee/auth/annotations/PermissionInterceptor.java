package site.zido.coffee.auth.annotations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import site.zido.coffee.auth.context.UserHolder;
import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.handlers.DisabledUserHandler;
import site.zido.coffee.auth.handlers.LoginExpectedHandler;
import site.zido.coffee.auth.handlers.UserManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionInterceptor implements HandlerInterceptor, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionInterceptor.class);
    private static Map<HandlerMethod, AuthVal> authCache = new ConcurrentHashMap<>();
    private LoginExpectedHandler loginExpectedHandler;
    private DisabledUserHandler disabledUserHandler;
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
        IUser currentUser = userManager.getCurrentUser(request);
        if (currentUser == null) {
            return false;
        }
        if (!currentUser.enabled()) {
            LOGGER.debug("current user is disabled");
            disabledUserHandler.handle(request, response);
            return false;
        }
        if (requiredRoles != null) {
            Collection<String> roles = currentUser.roles();
            if (noPermissions(requiredRoles, roles)) {
                loginExpectedHandler.handle(request, response);
                return false;
            }
        }
        UserHolder.set(currentUser);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.clearContext();
    }

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

    public void setLoginExpectedHandler(LoginExpectedHandler loginExpectedHandler) {
        this.loginExpectedHandler = loginExpectedHandler;
    }

    public void setDisabledUserHandler(DisabledUserHandler disabledUserHandler) {
        this.disabledUserHandler = disabledUserHandler;
    }

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
