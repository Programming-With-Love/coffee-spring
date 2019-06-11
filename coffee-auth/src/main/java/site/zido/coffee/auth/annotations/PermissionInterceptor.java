package site.zido.coffee.auth.annotations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import site.zido.coffee.auth.handlers.LoginExpectedHandler;
import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.handlers.UserManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionInterceptor.class);
    private static Map<HandlerMethod, AuthVal> authCache = new ConcurrentHashMap<>();
    private String userAttrName = "user";
    private LoginExpectedHandler loginExpectedHandler;

    private UserManager userManager;

    public PermissionInterceptor(LoginExpectedHandler loginExpectedHandler, UserManager userManager) {
        this.loginExpectedHandler = loginExpectedHandler;
        this.userManager = userManager;
    }

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
        Collection<String> requiredPermissions = authVal.getPermissions();
        Collection<String> requiredRoles = authVal.getRoles();
        IUser currentUser = userManager.getCurrentUser(request);
        if (currentUser == null) {
            return false;
        }
        if (requiredRoles != null) {
            Collection<String> roles = currentUser.roles();
            if (handlePermissions(requiredRoles, roles)) {
                loginExpectedHandler.handle(request, response);
                return false;
            }
        }
        if (requiredPermissions != null) {
            Collection<String> permissions = currentUser.roles();
            if (handlePermissions(requiredPermissions, permissions)) {
                loginExpectedHandler.handle(request, response);
                return false;
            }
        }
        request.setAttribute(userAttrName, currentUser);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (request.getAttribute(userAttrName) != null) {
            request.removeAttribute(userAttrName);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

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
        for (String permission : authAnnotation.permission()) {
            if (!"".equals(permission) && null != permission) {
                if (val.getPermissions() == null) {
                    val.setPermissions(new HashSet<>());
                }
                val.getPermissions().add(permission);
            }
        }
    }

    private boolean handlePermissions(Collection<String> needs, Collection<String> requires) {
        Set<String> all = new HashSet<>(requires);
        all.retainAll(needs);
        return all.isEmpty();
    }
}
