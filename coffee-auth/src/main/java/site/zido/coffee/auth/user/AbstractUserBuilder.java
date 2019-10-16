package site.zido.coffee.auth.user;

import site.zido.coffee.auth.core.GrantedAuthority;
import site.zido.coffee.auth.core.authority.SimpleGrantedAuthority;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static site.zido.coffee.auth.utils.CachedFieldUtils.getFieldValue;

public abstract class AbstractUserBuilder<T extends IUser> {
    private Field keyField;
    private Field accountNonExpiredField;
    private Field accountNonLockedField;
    private Field credentialsNonExpiredField;
    private Field enableField;
    private Field roleField;
    private Collection<GrantedAuthority> defaultRoles;

    public T build(Object user) {
        Object key = this.getKey(user);
        boolean enable = this.isEnabled(user);
        boolean accountNonExpired = this.isAccountNonExpired(user);
        boolean accountNonLocked = this.isAccountNonLocked(user);
        boolean credentialsNonExpired = this.isCredentialsNonExpired(user);
        Collection<? extends GrantedAuthority> authorities = this.getAuthorities(user);
        return doBuild(user,
                key,
                enable,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                authorities);
    }

    protected abstract T doBuild(Object user, Object key,
                                 boolean enable,
                                 boolean accountNonExpired,
                                 boolean credentialsNonExpired,
                                 boolean accountNonLocked,
                                 Collection<? extends GrantedAuthority> authorities);


    private Collection<? extends GrantedAuthority> getAuthorities(Object user) {
        if (user instanceof IUser) {
            return ((IUser) user).getAuthorities();
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
        if (target instanceof IUser) {
            return ((IUser) target).isEnabled();
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
            throw new IllegalStateException("consider set type of "
                    + target.getClass().getSimpleName() + "."
                    + enableField.getName()
                    + " as Boolean or Integer");
        } else {
            return true;
        }
    }

    public boolean isAccountNonExpired(Object user) {
        if (user instanceof IUser) {
            return ((IUser) user).isAccountNonExpired();
        }
        return true;
    }

    public boolean isAccountNonLocked(Object user) {
        if (user instanceof IUser) {
            return ((IUser) user).isAccountNonLocked();
        }
        return true;
    }

    public boolean isCredentialsNonExpired(Object user) {
        if (user instanceof IUser) {
            return ((IUser) user).isCredentialsNonExpired();
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
        if (user instanceof IUser) {
            return ((IUser) user).getKey();
        }
        return getFieldValue(user, keyField);
    }
}
