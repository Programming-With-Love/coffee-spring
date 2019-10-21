package site.zido.coffee.auth.config;

import site.zido.coffee.auth.authentication.UsernamePasswordAuthenticationProvider;
import site.zido.coffee.auth.security.PasswordEncoder;
import site.zido.coffee.auth.user.*;

/**
 * @author zido
 */
public class UsernamePasswordAuthenticationConfigurer
        <B extends ProviderManagerBuilder<B>,
                U extends IUserService<PasswordUser>>
        extends AbstractUserAwareConfigurer<B, U> {
    private U userService;
    private PasswordEncoder passwordEncoder;
    private IUserPasswordService passwordService;
    private Boolean hideUserNotFoundExceptions;
    private Class<?> userClass;

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
            AbstractJpaSupportedUserServiceImpl<PasswordUser> userService = postProcess(
                    new AbstractJpaSupportedUserServiceImpl<PasswordUser>(userClass,
                            reader.getPasswordField().getName()) {
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
}
