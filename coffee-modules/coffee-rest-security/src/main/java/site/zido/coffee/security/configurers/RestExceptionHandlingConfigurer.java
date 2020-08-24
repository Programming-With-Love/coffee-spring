package site.zido.coffee.security.configurers;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.RequestMatcherDelegatingAccessDeniedHandler;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
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

    public RestExceptionHandlingConfigurer() {
    }

    public RestExceptionHandlingConfigurer<H> accessDeniedHandler(
            AccessDeniedHandler accessDeniedHandler) {
        this.accessDeniedHandler = accessDeniedHandler;
        return this;
    }

    public RestExceptionHandlingConfigurer<H> defaultAccessDeniedHandlerFor(
            AccessDeniedHandler deniedHandler, RequestMatcher preferredMatcher) {
        this.defaultDeniedHandlerMappings.put(preferredMatcher, deniedHandler);
        return this;
    }

    public RestExceptionHandlingConfigurer<H> authenticationEntryPoint(
            AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        return this;
    }

    public RestExceptionHandlingConfigurer<H> defaultAuthenticationEntryPointFor(
            AuthenticationEntryPoint entryPoint, RequestMatcher preferredMatcher) {
        this.defaultEntryPointMappings.put(preferredMatcher, entryPoint);
        return this;
    }

    AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return this.authenticationEntryPoint;
    }

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

    AccessDeniedHandler getAccessDeniedHandler(H http) {
        AccessDeniedHandler deniedHandler = this.accessDeniedHandler;
        if (deniedHandler == null) {
            deniedHandler = createDefaultDeniedHandler(http);
        }
        return deniedHandler;
    }

    AuthenticationEntryPoint getAuthenticationEntryPoint(H http) {
        AuthenticationEntryPoint entryPoint = this.authenticationEntryPoint;
        if (entryPoint == null) {
            entryPoint = createDefaultEntryPoint(http);
        }
        return entryPoint;
    }

    private AccessDeniedHandler createDefaultDeniedHandler(H http) {
        if (this.defaultDeniedHandlerMappings.isEmpty()) {
            return new RestAccessDeniedHandlerImpl();
        }
        if (this.defaultDeniedHandlerMappings.size() == 1) {
            return this.defaultDeniedHandlerMappings.values().iterator().next();
        }
        return new RequestMatcherDelegatingAccessDeniedHandler(
                this.defaultDeniedHandlerMappings,
                new RestAccessDeniedHandlerImpl());
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
