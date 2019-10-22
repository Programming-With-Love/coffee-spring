package site.zido.coffee.auth.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import site.zido.coffee.auth.authentication.UsernamePasswordAuthenticationProvider;
import site.zido.coffee.auth.security.PasswordEncoder;
import site.zido.coffee.auth.user.*;

import javax.persistence.EntityManager;

/**
 * @author zido
 */
public class UsernamePasswordAuthenticationConfigurer
        <B extends ProviderManagerBuilder<B>,
                U extends IUserService<PasswordUser>>
        extends AbstractUserAwareConfigurer<B, U>
        implements ApplicationContextAware {
    private U userService;
    private PasswordEncoder passwordEncoder;
    private IUserPasswordService passwordService;
    private Boolean hideUserNotFoundExceptions;
    private Class<?> userClass;
    private ApplicationContext context;

    public UsernamePasswordAuthenticationConfigurer(Class<?> userClass) {
        this.userClass = userClass;
    }

    public UsernamePasswordAuthenticationConfigurer(U userService) {
        this.userService = userService;
    }

    @Override
    public U getUserService() {
        return userService;
    }

    @SuppressWarnings("unchecked")
    public <T extends UsernamePasswordAuthenticationConfigurer<B, U>> T passwordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UsernamePasswordAuthenticationConfigurer<B, U>> T userPasswordManager(IUserPasswordService passwordService) {
        this.passwordService = passwordService;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UsernamePasswordAuthenticationConfigurer<B, U>> T hideUserNotFoundException(boolean hideUserNotFoundException) {
        this.hideUserNotFoundExceptions = hideUserNotFoundException;
        return (T) this;
    }

    @Override
    public void configure(B builder) throws Exception {
        UsernamePasswordAuthenticationProvider provider;
        if (userService == null) {
            if (userClass == null) {
                throw new IllegalStateException("the user class and userService cannot be null at the same time");
            }
            PasswordUserReader reader = new PasswordUserReader(userClass);
            EntityManager em = context.getBean(EntityManager.class);
            AbstractJpaSupportedUserServiceImpl<PasswordUser> userService = postProcess(
                    new AbstractJpaSupportedUserServiceImpl<PasswordUser>(userClass,
                            reader.getPasswordField().getName(), em) {
                        @Override
                        protected PasswordUser packageUser(Object userEntity) {
                            return reader.parseUser(userEntity);
                        }
                    });
            provider = new UsernamePasswordAuthenticationProvider(userService);
        } else {
            provider = new UsernamePasswordAuthenticationProvider(userService);
        }
        provider = postProcess(provider);
        if (passwordService != null) {
            provider.setPasswordService(passwordService);
        }
        if (passwordEncoder != null) {
            provider.setPasswordEncoder(passwordEncoder);
        }
        if (hideUserNotFoundExceptions != null) {
            provider.setHideUserNotFoundExceptions(hideUserNotFoundExceptions);
        }
        builder.authenticationProvider(provider);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
