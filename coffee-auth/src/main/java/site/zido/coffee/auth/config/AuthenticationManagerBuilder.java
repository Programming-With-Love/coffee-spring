package site.zido.coffee.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.zido.coffee.auth.authentication.AuthenticationManager;
import site.zido.coffee.auth.authentication.AuthenticationProvider;
import site.zido.coffee.auth.authentication.ProviderManager;
import site.zido.coffee.auth.user.IUserService;
import site.zido.coffee.auth.user.PasswordUser;

import java.util.ArrayList;
import java.util.List;

/**
 * the builder of authentication manager
 *
 * @author zido
 */
public class AuthenticationManagerBuilder extends
        AbstractConfiguredAuthBuilder<AuthenticationManager, AuthenticationManagerBuilder>
        implements ProviderManagerBuilder<AuthenticationManagerBuilder> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Boolean eraseCredentials;
    private AuthenticationManager parentAuthenticationManager;
    private List<AuthenticationProvider> authenticationProviders = new ArrayList<>();

    public AuthenticationManagerBuilder(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor, true);
    }

    public AuthenticationManagerBuilder parentAuthenticationManager(AuthenticationManager authenticationManager) {
        if (authenticationManager instanceof ProviderManager) {
            eraseCredentials(((ProviderManager) authenticationManager)
                    .isEraseCredentialsAfterAuthentication());
        }
        this.parentAuthenticationManager = authenticationManager;
        return this;
    }

    public AuthenticationManagerBuilder eraseCredentials(boolean eraseCredentials) {
        this.eraseCredentials = eraseCredentials;
        return this;
    }

    public <T extends IUserService<PasswordUser>>
    UsernamePasswordAuthenticationConfigurer<AuthenticationManagerBuilder, IUserService<PasswordUser>> userService(T userService) throws Exception {
        return apply(new UsernamePasswordAuthenticationConfigurer<>(userService));
    }

    @Override
    protected AuthenticationManager performBuild() throws Exception {
        if (!isConfigured()) {
            logger.debug("No authenticationProviders and no parentAuthenticationManager defined. Returning null.");
            return null;
        }
        ProviderManager providerManager = new ProviderManager(authenticationProviders, parentAuthenticationManager);
        if (eraseCredentials != null) {
            providerManager.setEraseCredentialsAfterAuthentication(eraseCredentials);
        }
        providerManager = postProcess(providerManager);
        return providerManager;
    }

    private boolean isConfigured() {
        return !authenticationProviders.isEmpty() || parentAuthenticationManager != null;
    }

    @Override
    public AuthenticationManagerBuilder authenticationProvider(AuthenticationProvider provider) {
        this.authenticationProviders.add(provider);
        return this;
    }
}
