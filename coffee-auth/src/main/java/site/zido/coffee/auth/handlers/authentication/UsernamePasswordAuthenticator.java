package site.zido.coffee.auth.handlers.authentication;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.entity.annotations.AuthColumnPassword;
import site.zido.coffee.auth.entity.annotations.AuthColumnUsername;
import site.zido.coffee.auth.exceptions.AuthenticationException;
import site.zido.coffee.auth.exceptions.InternalAuthenticationException;
import site.zido.coffee.auth.exceptions.NotThisAuthenticatorException;
import site.zido.coffee.auth.exceptions.UsernamePasswordException;
import site.zido.coffee.auth.handlers.NoPasswordEncoder;
import site.zido.coffee.auth.handlers.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户名密码认证器，用于用户名密码登陆
 *
 * @author zido
 */
public class UsernamePasswordAuthenticator implements Authenticator {
    private static final String DEFAULT_USERNAME = "username";
    private static final String DEFAULT_PASSWORD = "password";
    private static final NoPasswordEncoder NO_PASSWORD_ENCODER_INSTANCE = new NoPasswordEncoder();
    private Map<Class<? extends IUser>, UsernamePasswordClassProps> propsCache =
            new HashMap<>();


    public UsernamePasswordAuthenticator() {
    }

    @Override
    public boolean prepare(Class<? extends IUser> userClass,
                           JpaRepository<? extends IUser, ? extends Serializable> repository) {
        if (propsCache.containsKey(userClass)) {
            return true;
        }
        UsernamePasswordClassProps props = new UsernamePasswordClassProps();
        props.setRepository(repository);
        ReflectionUtils.doWithFields(userClass, field -> {
            if (AnnotatedElementUtils.findMergedAnnotation(field, AuthColumnUsername.class) != null) {
                props.setUsernameField(field);
            } else if (props.getUsernameField() == null && field.getName().equals(DEFAULT_USERNAME)) {
                props.setUsernameField(field);
            }
            AuthColumnPassword passwordAnnotation;
            if ((passwordAnnotation = AnnotatedElementUtils.findMergedAnnotation(field, AuthColumnPassword.class)) != null) {
                Class<? extends PasswordEncoder> passwordEncoderClass = passwordAnnotation.encodeClass();
                try {
                    PasswordEncoder passwordEncoder = passwordEncoderClass.newInstance();
                    props.setPasswordEncoder(passwordEncoder);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                props.setPasswordField(field);
            } else if (props.getPasswordField() == null && field.getName().equals(DEFAULT_PASSWORD)) {
                props.setPasswordField(field);
                props.setPasswordEncoder(NO_PASSWORD_ENCODER_INSTANCE);
            }
        });
        props.setUserClass(userClass);
        return props.getUsernameField() != null
                && props.getPasswordField() != null
                && propsCache.put(userClass, props) == null;
    }

    @Override
    public IUser auth(HttpServletRequest request) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (!StringUtils.hasText(username) && !StringUtils.hasText(password)) {
            throw new NotThisAuthenticatorException();
        }
        username = username.trim();
        Collection<UsernamePasswordClassProps> values = propsCache.values();
        for (UsernamePasswordClassProps props : values) {
            try {
                IUser entity = props.getUserClass().newInstance();
                ReflectionUtils.setField(props.getUsernameField(), entity, username);
                Example example = Example.of(entity);
                IUser user = props.getRepository().findOne(example);
                if (user != null) {
                    String currentPassword = (String) ReflectionUtils.getField(props.getPasswordField(), entity);
                    if (props.getPasswordEncoder().validate(password, currentPassword)) {
                        return user;
                    }
                }
            } catch (InstantiationException | IllegalAccessException e) {
                throw new InternalAuthenticationException("加载用户时发生异常", e);
            }
        }
        throw new UsernamePasswordException();
    }

}
