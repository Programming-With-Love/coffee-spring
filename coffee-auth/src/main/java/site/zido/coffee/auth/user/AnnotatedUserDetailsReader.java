package site.zido.coffee.auth.user;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.annotation.Id;
import org.springframework.util.ReflectionUtils;
import site.zido.coffee.auth.Constants;
import site.zido.coffee.auth.core.GrantedAuthority;
import site.zido.coffee.auth.user.annotations.AuthColumnEnabled;
import site.zido.coffee.auth.user.annotations.AuthColumnKey;
import site.zido.coffee.auth.utils.CachedFieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 通过注解元信息提取用户接口的读取器
 *
 * @author zido
 */
public class AnnotatedUserDetailsReader implements UserDetailsReader {
    private Map<Class<?>, UserDetailsBuilder> builderCache = new ConcurrentHashMap<>(3);

    @Override
    public UserDetails parseUser(Object user) {
        if (user instanceof UserDetails) {
            return (UserDetails) user;
        }
        UserDetailsBuilder builder = builderCache.computeIfAbsent(user.getClass(), userClass -> {
            UserDetailsBuilder wrapper = new UserDetailsBuilder();
            ReflectionUtils.doWithFields(user.getClass(), field -> {
                //寻找id
                if (wrapper.getKeyField() == null
                        && (AnnotatedElementUtils.findMergedAnnotation(field, Id.class) != null
                        || AnnotatedElementUtils.findMergedAnnotation(field, javax.persistence.Id.class) != null)) {
                    wrapper.setKeyField(field);
                    return;
                }
                AuthColumnKey authColumnKeyAnnotation =
                        AnnotatedElementUtils.findMergedAnnotation(field, AuthColumnKey.class);
                if (authColumnKeyAnnotation != null) {
                    wrapper.setKeyField(field);
                }

                if (wrapper.getEnableField() != null
                        && "enable".equals(field.getName())) {
                    wrapper.setEnableField(field);
                }
                //寻找账户是否可用注解
                AuthColumnEnabled authColumnEnabledAnnotation =
                        AnnotatedElementUtils.findMergedAnnotation(field, AuthColumnEnabled.class);
                if (authColumnEnabledAnnotation != null) {
                    wrapper.setEnableField(field);
                }
            });
            return wrapper;
        });
        return builder.build(user);
    }


    private static class UserDetailsBuilder {
        private Field keyField;
        private Field accountNonExpiredField;
        private Field accountNonLockedField;
        private Field credentialsNonExpiredField;
        private Field enableField;

        private UserDetails build(Object user) {
            return new UserDetails() {
                private static final long serialVersionUID = Constants.COFFEE_AUTH_VERSION;

                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return null;
                }

                @Override
                public Object getKey() {
                    return UserDetailsBuilder.this.getKey(user);
                }

                @Override
                public boolean isAccountNonExpired() {
                    return UserDetailsBuilder.this.isAccountNonExpired(user);
                }

                @Override
                public boolean isAccountNonLocked() {
                    return UserDetailsBuilder.this.isAccountNonLocked(user);
                }

                @Override
                public boolean isCredentialsNonExpired() {
                    return UserDetailsBuilder.this.isAccountNonExpired(user);
                }
            };
        }

        public Field getKeyField() {
            return keyField;
        }

        public void setKeyField(Field keyField) {
            this.keyField = keyField;
        }

        public Field getAccountNonExpiredField() {
            return accountNonExpiredField;
        }

        public void setAccountNonExpiredField(Field accountNonExpiredField) {
            this.accountNonExpiredField = accountNonExpiredField;
        }

        public Field getAccountNonLockedField() {
            return accountNonLockedField;
        }

        public void setAccountNonLockedField(Field accountNonLockedField) {
            this.accountNonLockedField = accountNonLockedField;
        }

        public Field getCredentialsNonExpiredField() {
            return credentialsNonExpiredField;
        }

        public void setCredentialsNonExpiredField(Field credentialsNonExpiredField) {
            this.credentialsNonExpiredField = credentialsNonExpiredField;
        }

        public Field getEnableField() {
            return enableField;
        }

        public void setEnableField(Field enableField) {
            this.enableField = enableField;
        }

        public Object getKey(Object user) {
            if (user instanceof UserDetails) {
                return ((UserDetails) user).getKey();
            }
            return invoke(user, keyField);
        }

        @SuppressWarnings("unchecked")
        private <T> T invoke(Object target, Field field) {
            try {
                return (T) CachedFieldUtils.getGetterMethodByField(field, target.getClass()).invoke(target);
            } catch (IllegalAccessException | InvocationTargetException ignore) {
            }
            ReflectionUtils.makeAccessible(field);
            try {
                return (T) field.get(target);
            } catch (IllegalAccessException ignore) {
            }
            throw new RuntimeException("invoke get " + field.getName() + " error," +
                    "consider add a getter method for "
                    + target.getClass().getSimpleName() + "." + field.getName());
        }

        public boolean isEnabled(Object target) {
            if (target instanceof UserDetails) {
                return ((UserDetails) target).isEnabled();
            }
            Object value = invoke(target, enableField);
            if (value instanceof Boolean) {
                return (boolean) value;
            } else {
                if (value instanceof Number) {
                    return value.equals(1);
                }
            }
            throw new RuntimeException("consider set type of "
                    + target.getClass().getSimpleName() + "."
                    + enableField.getName()
                    + " as Boolean or Integer");
        }

        public boolean isAccountNonExpired(Object user) {
            if (user instanceof UserDetails) {
                return ((UserDetails) user).isAccountNonExpired();
            }
            return true;
        }

        public boolean isAccountNonLocked(Object user) {
            if (user instanceof UserDetails) {
                return ((UserDetails) user).isAccountNonLocked();
            }
            return true;
        }

        public boolean isCredentialsNonExpired(Object user) {
            if (user instanceof UserDetails) {
                return ((UserDetails) user).isCredentialsNonExpired();
            }
            return true;
        }


    }
}
