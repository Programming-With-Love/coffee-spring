package site.zido.coffee.auth.user;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.annotation.Id;
import org.springframework.util.ReflectionUtils;
import site.zido.coffee.auth.core.GrantedAuthority;
import site.zido.coffee.auth.core.authority.SimpleGrantedAuthority;
import site.zido.coffee.auth.user.annotations.AuthColumnEnabled;
import site.zido.coffee.auth.user.annotations.AuthColumnKey;
import site.zido.coffee.auth.user.annotations.AuthColumnRole;
import site.zido.coffee.auth.user.annotations.AuthEntity;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static site.zido.coffee.auth.utils.CachedFieldUtils.getFieldValue;

/**
 * 通过注解元信息提取用户接口的读取器
 *
 * @author zido
 */
public class AnnotatedUserDetailsReader implements UserDetailsReader {
    private Map<Class<?>, UserDetailsBuilder> builderCache = new ConcurrentHashMap<>(3);
    private static final AnnotatedUserDetailsReader INSTANCE = new AnnotatedUserDetailsReader();

    private AnnotatedUserDetailsReader() {
    }

    public static AnnotatedUserDetailsReader getInstance() {
        return INSTANCE;
    }

    public UserDetailsBuilder parse(Class userClass) {
        return builderCache.computeIfAbsent(userClass, clazz -> {
            UserDetailsBuilder udb = new UserDetailsBuilder();
            AuthEntity entityAnnotation = AnnotatedElementUtils.findMergedAnnotation(clazz, AuthEntity.class);
            if (entityAnnotation != null) {
                String[] roles = entityAnnotation.roles();
                udb.addDefaultRoles(roles);
            }
            ReflectionUtils.doWithFields(clazz, field -> {
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
            });
            return udb;
        });
    }

    @Override
    public UserDetails parseUser(Object user) {
        if (user instanceof UserDetails) {
            return (UserDetails) user;
        }
        UserDetailsBuilder builder = parse(user.getClass());
        return builder.build(user);
    }


    public static class UserDetailsBuilder {
        private Field keyField;
        private Field accountNonExpiredField;
        private Field accountNonLockedField;
        private Field credentialsNonExpiredField;
        private Field enableField;
        private Field roleField;
        private Collection<GrantedAuthority> defaultRoles;

        private UserDetails build(Object user) {
            Object key = this.getKey(user);
            boolean enable = this.isEnabled(user);
            boolean accountNonExpired = this.isAccountNonExpired(user);
            boolean accountNonLocked = this.isAccountNonLocked(user);
            boolean credentialsNonExpired = this.isCredentialsNonExpired(user);
            Collection<? extends GrantedAuthority> authorities = this.getAuthorities(user);
            return new User(key,
                    enable,
                    accountNonExpired,
                    credentialsNonExpired,
                    accountNonLocked,
                    authorities);
        }

        private Collection<? extends GrantedAuthority> getAuthorities(Object user) {
            if (user instanceof UserDetails) {
                return ((UserDetails) user).getAuthorities();
            }
            if (roleField != null) {
                Object result = getFieldValue(user, roleField);
                if (result instanceof String) {
                    List<GrantedAuthority> authorities = new ArrayList<>(defaultRoles);
                    authorities.add(new SimpleGrantedAuthority((String) result));
                    return Collections.unmodifiableCollection(authorities);
                }
            }
            return Collections.unmodifiableCollection(defaultRoles);
        }



        public boolean isEnabled(Object target) {
            if (target instanceof UserDetails) {
                return ((UserDetails) target).isEnabled();
            }
            if (enableField != null) {
                Object value = getFieldValue(target, enableField);
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
            } else {
                return true;
            }
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


        public void addDefaultRoles(String[] roles) {
            Collection<GrantedAuthority> collect =
                    Stream.of(roles).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            if (defaultRoles == null) {
                defaultRoles = collect;
            } else {
                defaultRoles.addAll(collect);
            }
        }

        public Field getRoleField() {
            return roleField;
        }

        public void setRoleField(Field roleField) {
            this.roleField = roleField;
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
            return getFieldValue(user, keyField);
        }
    }
}
