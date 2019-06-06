package site.zido.coffee.annotations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionInterceptor.class);
    private static Map<HandlerMethod, AuthVal> authCache = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod method = (HandlerMethod) handler;
        AuthVal authVal = determinerAuth(method);
        //TODO
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

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
                handleAuth(val, methodAuth);
            }
            if (classAuth != null) {
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
}
