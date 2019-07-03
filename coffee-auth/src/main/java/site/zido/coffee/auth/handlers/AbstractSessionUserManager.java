package site.zido.coffee.auth.handlers;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.annotation.Id;
import org.springframework.util.ReflectionUtils;
import site.zido.coffee.auth.Constants;
import site.zido.coffee.auth.context.UserHolder;
import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.entity.annotations.AuthColumnKey;
import site.zido.coffee.auth.utils.FieldUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于session的用户管理器
 *
 * @author zido
 */
public abstract class AbstractSessionUserManager implements UserManager {
    private static final Map<Class<? extends IUser>, Method> KEY_METHOD_CACHE
            = new ConcurrentHashMap<>();

    protected abstract IUser getUserByKey(Object key, Class<? extends IUser> userClass);

    @Override
    public IUser getCurrentUser(HttpServletRequest request) {
        IUser user = UserHolder.get();
        if (user != null) {
            return user;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object key = session.getAttribute(Constants.DEFAULT_SESSION_ATTRIBUTE_NAME);
        if (key == null) {
            return null;
        }
        Class<? extends IUser> userClass =
                (Class<? extends IUser>) session.getAttribute(Constants.DEFAULT_SESSION_ATTRIBUTE_NAME + ".class");
        return getUserByKey(key, userClass);
    }

    @Override
    public Collection<String> getRoles(IUser user) {
        return Collections.singleton(user.role());
    }

    @Override
    public void setUser(HttpServletRequest request, IUser authResult) {
        HttpSession session = request.getSession(true);
        Method method = KEY_METHOD_CACHE.computeIfAbsent(authResult.getClass(), clazz -> {
            Field[] fields = new Field[1];
            ReflectionUtils.doWithFields(clazz, field -> {
                //兼容jpa标准，默认使用id作为session内容
                Id id = AnnotatedElementUtils.findMergedAnnotation(field, Id.class);
                if (id != null && fields[0] == null) {
                    fields[0] = field;
                    return;
                }
                AuthColumnKey annotation =
                        AnnotatedElementUtils.findMergedAnnotation(field, AuthColumnKey.class);
                if (annotation != null) {
                    fields[0] = field;
                }
            }, field -> {
                AuthColumnKey annotation =
                        AnnotatedElementUtils.findMergedAnnotation(field, AuthColumnKey.class);
                Id id = AnnotatedElementUtils.findMergedAnnotation(field, Id.class);
                return annotation != null || id != null;
            });
            return FieldUtils.getGetterMethodByField(fields[0], clazz);
        });
        Object key = ReflectionUtils.invokeMethod(method, authResult);
        session.setAttribute(Constants.DEFAULT_SESSION_ATTRIBUTE_NAME, key);
        session.setAttribute(Constants.DEFAULT_SESSION_ATTRIBUTE_NAME + ".class",
                authResult.getClass());
    }

    @Override
    public void clear() {
        UserHolder.clearContext();
    }

}
