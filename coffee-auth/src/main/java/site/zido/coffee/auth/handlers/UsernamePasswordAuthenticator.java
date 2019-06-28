package site.zido.coffee.auth.handlers;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.ReflectionUtils;
import site.zido.coffee.auth.entity.annotations.AuthColumnPassword;
import site.zido.coffee.auth.entity.annotations.AuthColumnUsername;
import site.zido.coffee.auth.exceptions.InternalAuthenticationException;
import site.zido.coffee.auth.exceptions.UsernamePasswordException;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Field;

public class UsernamePasswordAuthenticator<T, ID extends Serializable> implements Authenticator<T, ID> {
    private static final String DEFAULT_USERNAME = "username";
    private static final String DEFAULT_PASSWORD = "password";

    private Class<T> userClass;
    private Field usernameField;
    private Field passwordField;
    private JpaRepository<T, ID> repository;
    private PasswordEncoder passwordEncoder;

    public UsernamePasswordAuthenticator(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean prepare(Class<T> userClass, JpaRepository<T, ID> repository) {
        this.repository = repository;
        Field[] fields = userClass.getDeclaredFields();
        Field usernameField = null;
        Field passwordField = null;
        for (Field field : fields) {
            if (AnnotatedElementUtils.findMergedAnnotation(field, AuthColumnUsername.class) != null) {
                usernameField = field;
            } else if (usernameField == null && field.getName().equals(DEFAULT_USERNAME)) {
                usernameField = field;
            }
            if (AnnotatedElementUtils.findMergedAnnotation(field, AuthColumnPassword.class) != null) {
                passwordField = field;
            } else if (passwordField == null && field.getName().equals(DEFAULT_PASSWORD)) {
                passwordField = field;
            }
        }
        this.usernameField = usernameField;
        this.passwordField = passwordField;
        this.userClass = userClass;
        return this.usernameField != null && passwordField != null;
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
        try {
            T entity = userClass.newInstance();
            ReflectionUtils.setField(usernameField, entity, username);
            Example<T> example = Example.of(entity);
            T user = repository.findOne(example);
            if (user != null) {
                String currentPassword = (String) ReflectionUtils.getField(passwordField, entity);
                if (passwordEncoder.validate(password, currentPassword)) {
                    return user;
                }
            }
            throw new UsernamePasswordException();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new InternalAuthenticationException("加载用户时发生异常", e);
        }
    }

}
