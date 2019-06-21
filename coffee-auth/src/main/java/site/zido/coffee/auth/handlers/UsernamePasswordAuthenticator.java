package site.zido.coffee.auth.handlers;

import org.springframework.core.annotation.AnnotatedElementUtils;
import site.zido.coffee.auth.entity.annotations.AuthColumnPassword;
import site.zido.coffee.auth.entity.annotations.AuthColumnUsername;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;

public class UsernamePasswordAuthenticator<T> implements Authenticator<T> {
    private static final String DEFAULT_USERNAME = "username";
    private static final String DEFAULT_PASSWORD = "password";

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
            } else if (usernameField != null && field.getName().equals(DEFAULT_USERNAME)) {
                usernameField = field;
            }
            if (AnnotatedElementUtils.findMergedAnnotation(field, AuthColumnPassword.class) != null) {
                passwordField = field;
            } else if (passwordField != null && field.getName().equals(DEFAULT_PASSWORD)) {
                passwordField = field;
            }
        }
        this.userClass = userClass;
        return false;
    }

    @Override
    public T auth(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        }
        username = username.trim();
        return null;
    }
}
