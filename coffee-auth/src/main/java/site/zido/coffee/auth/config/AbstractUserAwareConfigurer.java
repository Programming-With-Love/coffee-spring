package site.zido.coffee.auth.config;

import site.zido.coffee.auth.authentication.AuthenticationManager;
import site.zido.coffee.auth.user.IUserService;

/**
 * @author zido
 */
public abstract class AbstractUserAwareConfigurer<B extends ProviderManagerBuilder<B>, U extends IUserService>
        extends AuthConfigurerAdapter<AuthenticationManager, B> {

    public abstract U getUserService();
}
