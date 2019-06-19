package site.zido.coffee.auth.handlers;

import org.springframework.core.annotation.AnnotatedElementUtils;
import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.entity.annotations.AuthColumnPassword;
import site.zido.coffee.auth.entity.annotations.AuthColumnUsername;

import java.lang.reflect.Field;
import java.util.Map;

public class UsernamePasswordAuthenticator<T extends IUser> implements Authenticator<T> {
    private static final String defaultUsername = "username";
    private static final String defaultPassword = "password";

    private Class<T> userClass;
    private Field usernameField;
    private Field passwordField;

    @Override
    public boolean prepare(Class<T> userClass) {
        Field[] fields = userClass.getDeclaredFields();
        Field usernameField = null;
        Field passwordField = null;
        for (Field field : fields) {
            if (AnnotatedElementUtils.findMergedAnnotation(field, AuthColumnUsername.class) != null) {
                usernameField = field;
            } else if (usernameField != null && field.getName().equals(defaultUsername)) {
                usernameField = field;
            }
            if (AnnotatedElementUtils.findMergedAnnotation(field, AuthColumnPassword.class) != null) {
                passwordField = field;
            } else if (passwordField != null && field.getName().equals(defaultPassword)) {
                passwordField = field;
            }
        }
        this.userClass = userClass;
        return false;
    }

    @Override
    public T auth(Map<String, String> params) {
        return null;
    }
}
