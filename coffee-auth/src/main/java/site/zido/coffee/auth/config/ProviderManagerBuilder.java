package site.zido.coffee.auth.config;

import site.zido.coffee.auth.authentication.AuthenticationManager;
import site.zido.coffee.auth.authentication.AuthenticationProvider;

/**
 * @author zido
 */
public interface ProviderManagerBuilder<B extends ProviderManagerBuilder<B>>
        extends AuthBuilder<AuthenticationManager> {

    B authenticationProvider(AuthenticationProvider provider);
}
