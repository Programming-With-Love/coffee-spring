package site.zido.coffee.auth.config;

import site.zido.coffee.auth.authentication.AuthenticationProvider;
import site.zido.coffee.auth.user.IUserService;
import site.zido.coffee.auth.web.UrlBasedFilterChainManager;

import javax.servlet.Filter;

public interface HttpAuthBuilder<H extends HttpAuthBuilder<H>> extends
        AuthBuilder<UrlBasedFilterChainManager> {
    <C extends AuthConfigurer<UrlBasedFilterChainManager, H>> C getConfigurer(Class<C> clazz);

    <C extends AuthConfigurer<UrlBasedFilterChainManager, H>> C removeConfigurer(Class<C> clazz);

    <C> void setSharedObject(Class<C> sharedObject, C object);

    <C> C getSharedObject(Class<C> sharedType);

    H authenticationProvider(AuthenticationProvider provider);

    H userService(IUserService userService) throws Exception;

    H addFilterAfter(Filter filter, Class<? extends Filter> afterFilter);

    H addFilterBefore(Filter filter, Class<? extends Filter> beforeFilter);

    H addFilter(Filter filter);
}
