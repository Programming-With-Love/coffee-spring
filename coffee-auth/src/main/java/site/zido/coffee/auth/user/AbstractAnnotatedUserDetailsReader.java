package site.zido.coffee.auth.user;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.annotation.Id;
import org.springframework.util.ReflectionUtils;
import site.zido.coffee.auth.user.annotations.AuthColumnEnabled;
import site.zido.coffee.auth.user.annotations.AuthColumnKey;
import site.zido.coffee.auth.user.annotations.AuthColumnRole;
import site.zido.coffee.auth.user.annotations.AuthEntity;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通过注解元信息提取用户接口的读取器
 *
 * @author zido
 */
public abstract class AbstractAnnotatedUserDetailsReader<T extends IUser, B extends AbstractUserBuilder<T>>
        implements UserDetailsReader<T> {
    protected B builder;

    public AbstractAnnotatedUserDetailsReader(Class<?> userClass) {
        builder = parse(userClass);
    }

    private B parse(Class userClass) {
        B udb = createBuilder(userClass);
        AuthEntity entityAnnotation = AnnotatedElementUtils.findMergedAnnotation(userClass, AuthEntity.class);
        if (entityAnnotation != null) {
            String[] roles = entityAnnotation.roles();
            udb.addDefaultRoles(roles);
        }
        ReflectionUtils.doWithFields(userClass, field -> {
            //寻找id
            if (udb.getKeyField() == null
                    && (AnnotatedElementUtils.findMergedAnnotation(field, Id.class) != null
                    || AnnotatedElementUtils.findMergedAnnotation(field, javax.persistence.Id.class) != null)) {
                udb.setKeyField(field);
                return;
            }
            AuthColumnKey authColumnKeyAnnotation =
                    AnnotatedElementUtils.findMergedAnnotation(field, AuthColumnKey.class);
            if (authColumnKeyAnnotation != null) {
                udb.setKeyField(field);
            }

            if (udb.getEnableField() != null
                    && "enable".equals(field.getName())) {
                udb.setEnableField(field);
            }
            //寻找账户是否可用注解
            AuthColumnEnabled authColumnEnabledAnnotation =
                    AnnotatedElementUtils.findMergedAnnotation(field, AuthColumnEnabled.class);
            if (authColumnEnabledAnnotation != null) {
                udb.setEnableField(field);
            }

            if (udb.getRoleField() == null
                    && "role".equals(field.getName())) {
                udb.setRoleField(field);
            }
            AuthColumnRole authColumnRoleAnnotation = AnnotatedElementUtils.findMergedAnnotation(field, AuthColumnRole.class);
            if (authColumnRoleAnnotation != null) {
                udb.setRoleField(field);
            }
            additionalParse(udb, userClass, field);
        });
        return udb;
    }

    protected abstract void additionalParse(B udb, Class<?> userClass, Field field);

    protected abstract B createBuilder(Class<?> userClass);

    @Override
    @SuppressWarnings("unchecked")
    public T parseUser(Object user) {
        if (user instanceof IUser) {
            return (T) user;
        }
        return (T) builder.build(user);
    }


}
