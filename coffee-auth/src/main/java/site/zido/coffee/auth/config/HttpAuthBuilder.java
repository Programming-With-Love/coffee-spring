package site.zido.coffee.auth.config;

import site.zido.coffee.auth.authentication.AuthenticationProvider;
import site.zido.coffee.auth.user.IUserService;
import site.zido.coffee.auth.web.UrlBasedFilterChainManager;

import javax.servlet.Filter;

/**
 * @author zido
 */
public interface HttpAuthBuilder<H extends HttpAuthBuilder<H>> extends
        AuthBuilder<UrlBasedFilterChainManager> {
    <C extends AuthConfigurer<UrlBasedFilterChainManager, H>> C getConfigurer(Class<C> clazz);

    <C extends AuthConfigurer<UrlBasedFilterChainManager, H>> C removeConfigurer(Class<C> clazz);

    H authenticationProvider(AuthenticationProvider provider);

    H userService(IUserService userService) throws Exception;

    H addFilter(Filter filter);
}
