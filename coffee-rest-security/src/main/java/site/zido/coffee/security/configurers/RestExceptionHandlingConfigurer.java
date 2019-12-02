package site.zido.coffee.security.configurers;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.RequestMatcherDelegatingAccessDeniedHandler;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.matcher.RequestMatcher;
import site.zido.coffee.security.token.JwtAuthenticationEntryPoint;

import java.util.LinkedHashMap;

/**
 * @author zido
 */
public class RestExceptionHandlingConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractHttpConfigurer<RestExceptionHandlingConfigurer<H>, H> {
    private AuthenticationEntryPoint authenticationEntryPoint;

    private AccessDeniedHandler accessDeniedHandler;

    private LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> defaultEntryPointMappings = new LinkedHashMap<>();

    private LinkedHashMap<RequestMatcher, AccessDeniedHandler> defaultDeniedHandlerMappings = new LinkedHashMap<>();

    /**
     * Creates a new instance
     *
     * @see HttpSecurity#exceptionHandling()
     */
    public RestExceptionHandlingConfigurer() {
    }

    /**
     * Shortcut to specify the {@link AccessDeniedHandler} to be used is a specific error
     * page
     *
     * @param accessDeniedUrl the URL to the access denied page (i.e. /errors/401)
     * @return the {@link ExceptionHandlingConfigurer} for further customization
     * @see AccessDeniedHandlerImpl
     * @see #accessDeniedHandler(org.springframework.security.web.access.AccessDeniedHandler)
     */
    public RestExceptionHandlingConfigurer<H> accessDeniedPage(String accessDeniedUrl) {
        AccessDeniedHandlerImpl accessDeniedHandler = new AccessDeniedHandlerImpl();
        accessDeniedHandler.setErrorPage(accessDeniedUrl);
        return accessDeniedHandler(accessDeniedHandler);
    }

    /**
     * Specifies the {@link AccessDeniedHandler} to be used
     *
     * @param accessDeniedHandler the {@link AccessDeniedHandler} to be used
     * @return the {@link ExceptionHandlingConfigurer} for further customization
     */
    public RestExceptionHandlingConfigurer<H> accessDeniedHandler(
            AccessDeniedHandler accessDeniedHandler) {
        this.accessDeniedHandler = accessDeniedHandler;
        return this;
    }

    /**
     * Sets a default {@link AccessDeniedHandler} to be used which prefers being
     * invoked for the provided {@link RequestMatcher}. If only a single default
     * {@link AccessDeniedHandler} is specified, it will be what is used for the
     * default {@link AccessDeniedHandler}. If multiple default
     * {@link AccessDeniedHandler} instances are configured, then a
     * {@link RequestMatcherDelegatingAccessDeniedHandler} will be used.
     *
     * @param deniedHandler    the {@link AccessDeniedHandler} to use
     * @param preferredMatcher the {@link RequestMatcher} for this default
     *                         {@link AccessDeniedHandler}
     * @return the {@link ExceptionHandlingConfigurer} for further customizations
     * @since 5.1
     */
    public RestExceptionHandlingConfigurer<H> defaultAccessDeniedHandlerFor(
            AccessDeniedHandler deniedHandler, RequestMatcher preferredMatcher) {
        this.defaultDeniedHandlerMappings.put(preferredMatcher, deniedHandler);
        return this;
    }

    /**
     * Sets the {@link AuthenticationEntryPoint} to be used.
     *
     * <p>
     * If no {@link #authenticationEntryPoint(AuthenticationEntryPoint)} is specified,
     * then
     * {@link #defaultAuthenticationEntryPointFor(AuthenticationEntryPoint, RequestMatcher)}
     * will be used. The first {@link AuthenticationEntryPoint} will be used as the
     * default if no matches were found.
     * </p>
     *
     * <p>
     * If that is not provided defaults to {@link Http403ForbiddenEntryPoint}.
     * </p>
     *
     * @param authenticationEntryPoint the {@link AuthenticationEntryPoint} to use
     * @return the {@link ExceptionHandlingConfigurer} for further customizations
     */
    public RestExceptionHandlingConfigurer<H> authenticationEntryPoint(
            AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        return this;
    }

    /**
     * Sets a default {@link AuthenticationEntryPoint} to be used which prefers being
     * invoked for the provided {@link RequestMatcher}. If only a single default
     * {@link AuthenticationEntryPoint} is specified, it will be what is used for the
     * default {@link AuthenticationEntryPoint}. If multiple default
     * {@link AuthenticationEntryPoint} instances are configured, then a
     * {@link DelegatingAuthenticationEntryPoint} will be used.
     *
     * @param entryPoint       the {@link AuthenticationEntryPoint} to use
     * @param preferredMatcher the {@link RequestMatcher} for this default
     *                         {@link AuthenticationEntryPoint}
     * @return the {@link ExceptionHandlingConfigurer} for further customizations
     */
    public RestExceptionHandlingConfigurer<H> defaultAuthenticationEntryPointFor(
            AuthenticationEntryPoint entryPoint, RequestMatcher preferredMatcher) {
        this.defaultEntryPointMappings.put(preferredMatcher, entryPoint);
        return this;
    }

    /**
     * Gets any explicitly configured {@link AuthenticationEntryPoint}
     *
     * @return
     */
    AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return this.authenticationEntryPoint;
    }

    /**
     * Gets the {@link AccessDeniedHandler} that is configured.
     *
     * @return the {@link AccessDeniedHandler}
     */
    AccessDeniedHandler getAccessDeniedHandler() {
        return this.accessDeniedHandler;
    }

    @Override
    public void configure(H http) {
        AuthenticationEntryPoint entryPoint = getAuthenticationEntryPoint(http);
        ExceptionTranslationFilter exceptionTranslationFilter = new ExceptionTranslationFilter(
                entryPoint, getRequestCache(http));
        AccessDeniedHandler deniedHandler = getAccessDeniedHandler(http);
        exceptionTranslationFilter.setAccessDeniedHandler(deniedHandler);
        exceptionTranslationFilter = postProcess(exceptionTranslationFilter);
        http.addFilter(exceptionTranslationFilter);
    }

    /**
     * Gets the {@link AccessDeniedHandler} according to the rules specified by
     * {@link #accessDeniedHandler(AccessDeniedHandler)}
     *
     * @param http the {@link HttpSecurity} used to look up shared
     *             {@link AccessDeniedHandler}
     * @return the {@link AccessDeniedHandler} to use
     */
    AccessDeniedHandler getAccessDeniedHandler(H http) {
        AccessDeniedHandler deniedHandler = this.accessDeniedHandler;
        if (deniedHandler == null) {
            deniedHandler = createDefaultDeniedHandler(http);
        }
        return deniedHandler;
    }

    /**
     * Gets the {@link AuthenticationEntryPoint} according to the rules specified by
     * {@link #authenticationEntryPoint(AuthenticationEntryPoint)}
     *
     * @param http the {@link HttpSecurity} used to look up shared
     *             {@link AuthenticationEntryPoint}
     * @return the {@link AuthenticationEntryPoint} to use
     */
    AuthenticationEntryPoint getAuthenticationEntryPoint(H http) {
        AuthenticationEntryPoint entryPoint = this.authenticationEntryPoint;
        if (entryPoint == null) {
            entryPoint = createDefaultEntryPoint(http);
        }
        return entryPoint;
    }

    private AccessDeniedHandler createDefaultDeniedHandler(H http) {
        if (this.defaultDeniedHandlerMappings.isEmpty()) {
            return new AccessDeniedHandlerImpl();
        }
        if (this.defaultDeniedHandlerMappings.size() == 1) {
            return this.defaultDeniedHandlerMappings.values().iterator().next();
        }
        return new RequestMatcherDelegatingAccessDeniedHandler(
                this.defaultDeniedHandlerMappings,
                new AccessDeniedHandlerImpl());
    }

    private AuthenticationEntryPoint createDefaultEntryPoint(H http) {
        if (this.defaultEntryPointMappings.isEmpty()) {
            return new JwtAuthenticationEntryPoint();
        }
        if (this.defaultEntryPointMappings.size() == 1) {
            return this.defaultEntryPointMappings.values().iterator().next();
        }
        DelegatingAuthenticationEntryPoint entryPoint = new DelegatingAuthenticationEntryPoint(
                this.defaultEntryPointMappings);
        entryPoint.setDefaultEntryPoint(this.defaultEntryPointMappings.values().iterator()
                .next());
        return entryPoint;
    }

    /**
     * Gets the {@link RequestCache} to use. If one is defined using
     * {@link #requestCache(org.springframework.security.web.savedrequest.RequestCache)},
     * then it is used. Otherwise, an attempt to find a {@link RequestCache} shared object
     * is made. If that fails, an {@link HttpSessionRequestCache} is used
     *
     * @param http the {@link HttpSecurity} to attempt to fined the shared object
     * @return the {@link RequestCache} to use
     */
    private RequestCache getRequestCache(H http) {
        RequestCache result = http.getSharedObject(RequestCache.class);
        if (result != null) {
            return result;
        }
        return new NullRequestCache();
    }

}
