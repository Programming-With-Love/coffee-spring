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
    private static final Map<Class<? extends IUser>, FieldVal> KEY_METHOD_CACHE
            = new ConcurrentHashMap<>();

    protected abstract IUser getUserByKey(Object fieldValue, String fieldName, Class<? extends IUser> userClass);

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
                (Class<? extends IUser>) session.getAttribute(
                        getClassAttrName(Constants.DEFAULT_SESSION_ATTRIBUTE_NAME));
        String name = (String) session.getAttribute(
                getFieldNameAttrName(Constants.DEFAULT_SESSION_ATTRIBUTE_NAME));
        return getUserByKey(key, name, userClass);
    }

    @Override
    public Collection<String> getRoles(IUser user) {
        return Collections.singleton(user.role());
    }

    @Override
    public void setUser(HttpServletRequest request, IUser authResult) {
        HttpSession session = request.getSession(true);
        FieldVal val = KEY_METHOD_CACHE.computeIfAbsent(authResult.getClass(), clazz -> {
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
            return new FieldVal(fields[0], FieldUtils.getGetterMethodByField(fields[0], clazz));
        });
        Object key = ReflectionUtils.invokeMethod(val.getMethod(), authResult);
        session.setAttribute(Constants.DEFAULT_SESSION_ATTRIBUTE_NAME, key);
        session.setAttribute(
                getClassAttrName(Constants.DEFAULT_SESSION_ATTRIBUTE_NAME),
                authResult.getClass());
        session.setAttribute(
                getFieldNameAttrName(Constants.DEFAULT_SESSION_ATTRIBUTE_NAME),
                val.getField().getName());
    }

    protected String getClassAttrName(String attrName) {
        return attrName + ".class";
    }

    protected String getFieldNameAttrName(String attrName) {
        return attrName + ".name";
    }

    @Override
    public void clear() {
        UserHolder.clearContext();
    }


}
