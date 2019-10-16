package site.zido.coffee.auth.user;

import site.zido.coffee.auth.core.GrantedAuthority;

import java.lang.reflect.Field;
import java.util.Collection;

import static site.zido.coffee.auth.utils.CachedFieldUtils.getFieldValue;

public class PasswordUserBuilder extends AbstractUserBuilder<PasswordUser> {
    private Field passwordField;

    @Override
    protected PasswordUser doBuild(Object user, Object key,
                                   boolean enable,
                                   boolean accountNonExpired,
                                   boolean credentialsNonExpired,
                                   boolean accountNonLocked,
                                   Collection<? extends GrantedAuthority> authorities) {
        String password = getPassword(user);
        return new PasswordUser(key,
                password,
                enable,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                authorities);
    }

    public String getPassword(Object target) {
        if (target instanceof PasswordUser) {
            return ((PasswordUser) target).getPassword();
        }
        if (passwordField != null) {
            Object value = getFieldValue(target, passwordField);
            if (value instanceof String) {
                return (String) value;
            }
        }
        throw new IllegalStateException("consider set type of "
                + target.getClass().getSimpleName() + "."
                + passwordField.getName()
                + " as String");
    }

    public void setPasswordField(Field passwordField) {
        this.passwordField = passwordField;
    }

    public Field getPasswordField() {
        return passwordField;
    }
}
