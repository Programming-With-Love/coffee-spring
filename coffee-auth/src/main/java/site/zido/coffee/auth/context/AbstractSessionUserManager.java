package site.zido.coffee.auth.context;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.annotation.Id;
import org.springframework.util.ReflectionUtils;
import site.zido.coffee.auth.Constants;
import site.zido.coffee.auth.core.Authentication;
import site.zido.coffee.auth.user.IUser;
import site.zido.coffee.auth.user.annotations.AuthColumnKey;
import site.zido.coffee.auth.handlers.FieldVal;
import site.zido.coffee.auth.utils.CachedFieldUtils;

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
    protected String sessionKey = Constants.DEFAULT_SESSION_ATTRIBUTE_NAME;

    /**
     * 通过键值从数据源查询用户
     *
     * @param fieldValue 查询值
     * @param fieldName  查询键
     * @param userClass  查询目标类
     * @return user
     */
    protected abstract Authentication getUserByKey(Object fieldValue, String fieldName, Class<? extends IUser> userClass);

    @Override
    @SuppressWarnings("unchecked")
    public Authentication getCurrentUser(HttpServletRequest request) {
        Authentication user = UserHolder.get().getAuthentication();
        if (user != null) {
            return user;
        }
        HttpSession session;
        Object key;
        if ((session = request.getSession(false)) == null
                || (key = session.getAttribute(sessionKey)) == null) {
            return null;
        }
        Class<? extends IUser> userClass =
                (Class<? extends IUser>) session.getAttribute(
                        getClassAttrName(sessionKey));
        String name = (String) session.getAttribute(
                getFieldNameAttrName(sessionKey));
        user = getUserByKey(key, name, userClass);
        if (user != null) {
            UserHolder.set(new UserContextImpl(user));
        }
        return user;
    }

    @Override
    public Collection<String> getRoles(Authentication user) {
        return Collections.singleton(user.role());
    }

    /**
     * 绑定用户到对应的session中
     *
     * @param request    request
     * @param authResult 用户
     */
    @Override
    public void bindUser(HttpServletRequest request, IUser authResult) {
        //反射缓存查询
        FieldVal val = KEY_METHOD_CACHE.computeIfAbsent(authResult.getClass(), clazz -> {
            Field[] fields = new Field[1];
            ReflectionUtils.doWithFields(clazz, field -> {
                //兼容jpa标准，默认使用id作为session内容
                if (fields[0] == null
                        && (AnnotatedElementUtils.findMergedAnnotation(field, Id.class) != null
                        || AnnotatedElementUtils.findMergedAnnotation(field, javax.persistence.Id.class) != null)) {
                    fields[0] = field;
                    return;
                }
                AuthColumnKey annotation =
                        AnnotatedElementUtils.findMergedAnnotation(field, AuthColumnKey.class);
                if (annotation != null) {
                    fields[0] = field;
                }
            });
            if (fields[0] == null) {
                throw new IllegalStateException("the user entity should by annotated by javax.persistence.Id" +
                        " or org.springframework.data.annotation" +
                        " or site.zido.coffee.auth.entity.annotations.AuthColumnKey");
            }
            return new FieldVal(fields[0], CachedFieldUtils.getGetterMethodByField(fields[0], clazz));
        });
        Object key = ReflectionUtils.invokeMethod(val.getMethod(), authResult);
        HttpSession session = request.getSession(true);
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

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    @Override
    public void clear() {
        UserHolder.clearContext();
    }


}
