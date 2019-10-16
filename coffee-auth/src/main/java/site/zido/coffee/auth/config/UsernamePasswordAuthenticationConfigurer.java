package site.zido.coffee.auth.config;

import site.zido.coffee.auth.authentication.UsernamePasswordAuthenticationProvider;
import site.zido.coffee.auth.security.PasswordEncoder;
import site.zido.coffee.auth.user.*;

/**
 * @author zido
 */
public class UsernamePasswordAuthenticationConfigurer
        <B extends ProviderManagerBuilder<B>,
                C extends UsernamePasswordAuthenticationConfigurer<B, C, U>,
                U extends IUserService<PasswordUser>>
        extends AbstractUserAwareConfigurer<B, U> {
    private final U userService;
    private PasswordEncoder passwordEncoder;
    private IUserPasswordService passwordService;
    private Boolean hideUserNotFoundExceptions;
    private Class<?> userClass;

    public UsernamePasswordAuthenticationConfigurer(Class<?> userClass,
                                                    U userService) {
        this.userClass = userClass;
        this.userService = userService;
    }

    @Override
    public U getUserService() {
        return userService;
    }

    @SuppressWarnings("unchecked")
    public C passwordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C userPasswordManager(IUserPasswordService passwordService) {
        this.passwordService = passwordService;
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C hideUserNotFoundException(boolean hideUserNotFoundException) {
        this.hideUserNotFoundExceptions = hideUserNotFoundException;
        return (C) this;
    }

    @Override
    public void configure(B builder) throws Exception {
        UsernamePasswordAuthenticationProvider provider;
        if (userService == null) {
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
