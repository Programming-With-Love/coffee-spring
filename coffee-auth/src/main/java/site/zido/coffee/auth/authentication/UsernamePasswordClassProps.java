package site.zido.coffee.auth.authentication;

import site.zido.coffee.auth.user.IDUser;
import site.zido.coffee.auth.security.PasswordEncoder;

import java.lang.reflect.Field;

/**
 * 用户名密码相关的属性
 *
 * @author zido
 */
public class UsernamePasswordClassProps {
    private Class<? extends IDUser> userClass;
    private Field usernameField;
    private Field passwordField;
    private PasswordEncoder passwordEncoder;

    public Class<? extends IDUser> getUserClass() {
        return userClass;
    }

    public void setUserClass(Class<? extends IDUser> userClass) {
        this.userClass = userClass;
    }

    public Field getUsernameField() {
        return usernameField;
    }

    public void setUsernameField(Field usernameField) {
        this.usernameField = usernameField;
    }

    public Field getPasswordField() {
        return passwordField;
    }

    public void setPasswordField(Field passwordField) {
        this.passwordField = passwordField;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
