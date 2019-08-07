package site.zido.coffee.auth.authentication;

import org.springframework.data.jpa.repository.JpaRepository;
import site.zido.coffee.auth.user.IUser;
import site.zido.coffee.auth.security.PasswordEncoder;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * 用户名密码相关的属性
 *
 * @author zido
 */
public class UsernamePasswordClassProps {
    private Class<? extends IUser> userClass;
    private Field usernameField;
    private Field passwordField;
    private JpaRepository<? extends IUser, ? extends Serializable> repository;
    private PasswordEncoder passwordEncoder;

    public Class<? extends IUser> getUserClass() {
        return userClass;
    }

    public void setUserClass(Class<? extends IUser> userClass) {
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

    public JpaRepository<? extends IUser, ? extends Serializable> getRepository() {
        return repository;
    }

    public void setRepository(JpaRepository<? extends IUser, ? extends Serializable> repository) {
        this.repository = repository;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
