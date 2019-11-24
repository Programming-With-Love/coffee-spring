package org.springframework.security.config.annotation.web.configurers;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.*;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import site.zido.coffee.security.authentication.RestAuthenticationFailureHandler;
import site.zido.coffee.security.authentication.RestAuthenticationSuccessHandler;
import site.zido.coffee.security.jwt.JwtAuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author zido
 */
public abstract class AbstractRestAuthenticationFilterConfigurer<B extends HttpSecurityBuilder<B>, T extends AbstractRestAuthenticationFilterConfigurer<B, T, F>, F extends AbstractAuthenticationProcessingFilter>
        extends AbstractHttpConfigurer<T, B> {
    private F authFilter;

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

    private RestAuthenticationSuccessHandler defaultSuccessHandler = new RestAuthenticationSuccessHandler();
    private AuthenticationSuccessHandler successHandler = this.defaultSuccessHandler;

    private JwtAuthenticationEntryPoint authenticationEntryPoint = new JwtAuthenticationEntryPoint();

    private String loginProcessingUrl;

    private RestAuthenticationFailureHandler defaultFailureHandler = new RestAuthenticationFailureHandler();
    private AuthenticationFailureHandler failureHandler = this.defaultFailureHandler;

    private boolean permitAll;

    /**
     * Creates a new instance with minimal defaults
     */
    protected AbstractRestAuthenticationFilterConfigurer() {
    }

    /**
     * Creates a new instance
     *
     * @param authenticationFilter      the {@link AbstractAuthenticationProcessingFilter} to
     *                                  use
     * @param defaultLoginProcessingUrl the default URL to use for
     *                                  {@link #loginProcessingUrl(String)}
     */
    protected AbstractRestAuthenticationFilterConfigurer(F authenticationFilter,
                                                         String defaultLoginProcessingUrl) {
        this();
        this.authFilter = authenticationFilter;
        if (defaultLoginProcessingUrl != null) {
            loginProcessingUrl(defaultLoginProcessingUrl);
        }
    }

    public final T json(String jsonBody) {
        return defaultSuccessMessage(MediaType.APPLICATION_JSON, jsonBody);
    }

    public final T defaultSuccessMessage(MediaType mediaType, String message) {
        RestAuthenticationSuccessHandler handler = new RestAuthenticationSuccessHandler();
        handler.setMediaType(mediaType);
        handler.setSuccessBody(message);
        this.defaultSuccessHandler = handler;
        return successHandler(handler);
    }

    /**
     * Specifies the URL to validate the credentials.
     *
     * @param loginProcessingUrl the URL to validate username and password
     * @return the {@link FormLoginConfigurer} for additional customization
     */
    public T loginProcessingUrl(String loginProcessingUrl) {
        this.loginProcessingUrl = loginProcessingUrl;
        authFilter
                .setRequiresAuthenticationRequestMatcher(createLoginProcessingUrlMatcher(loginProcessingUrl));
        return getSelf();
    }

    /**
     * Create the {@link RequestMatcher} given a loginProcessingUrl
     *
     * @param loginProcessingUrl creates the {@link RequestMatcher} based upon the
     *                           loginProcessingUrl
     * @return the {@link RequestMatcher} to use based upon the loginProcessingUrl
     */
    protected abstract RequestMatcher createLoginProcessingUrlMatcher(
            String loginProcessingUrl);

    /**
     * Specifies a custom {@link AuthenticationDetailsSource}. The default is
     * {@link WebAuthenticationDetailsSource}.
     *
     * @param authenticationDetailsSource the custom {@link AuthenticationDetailsSource}
     * @return the {@link FormLoginConfigurer} for additional customization
     */
    public final T authenticationDetailsSource(
            AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        this.authenticationDetailsSource = authenticationDetailsSource;
        return getSelf();
    }

    /**
     * Specifies the {@link AuthenticationSuccessHandler} to be used. The default is
     * {@link SavedRequestAwareAuthenticationSuccessHandler} with no additional properites
     * set.
     *
     * @param successHandler the {@link AuthenticationSuccessHandler}.
     * @return the {@link FormLoginConfigurer} for additional customization
     */
    public final T successHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
        return getSelf();
    }

    /**
     * Equivalent of invoking permitAll(true)
     *
     * @return the {@link FormLoginConfigurer} for additional customization
     */
    public final T permitAll() {
        return permitAll(true);
    }

    public final T permitAll(boolean permitAll) {
        this.permitAll = permitAll;
        return getSelf();
    }

    public final T defaultFailureMessage(MediaType mediaType, String message) {
        RestAuthenticationFailureHandler handler = new RestAuthenticationFailureHandler();
        handler.setMediaType(mediaType);
        handler.setSuccessBody(message);
        this.defaultFailureHandler = handler;
        return failureHandler(handler);
    }

    public final T failureMessage(MediaType mediaType, String message) {
        return failureHandler(new RestAuthenticationFailureHandler(mediaType, message));
    }

    /**
     * Specifies the {@link AuthenticationFailureHandler} to use when authentication
     * fails. The default is redirecting to "/login?error" using
     * {@link SimpleUrlAuthenticationFailureHandler}
     *
     * @param authenticationFailureHandler the {@link AuthenticationFailureHandler} to use
     *                                     when authentication fails.
     * @return the {@link FormLoginConfigurer} for additional customization
     */
    public final T failureHandler(
            AuthenticationFailureHandler authenticationFailureHandler) {
        this.failureHandler = authenticationFailureHandler;
        return getSelf();
    }

    @Override
    public void init(B http) throws Exception {
        updateAccessDefaults(http);
        registerDefaultAuthenticationEntryPoint(http);
    }

    @SuppressWarnings("unchecked")
    protected final void registerDefaultAuthenticationEntryPoint(B http) {
        registerAuthenticationEntryPoint(http, this.authenticationEntryPoint);
    }

    @SuppressWarnings("unchecked")
    protected final void registerAuthenticationEntryPoint(B http, AuthenticationEntryPoint authenticationEntryPoint) {
        ExceptionHandlingConfigurer<B> exceptionHandling = http
                .getConfigurer(ExceptionHandlingConfigurer.class);
        if (exceptionHandling == null) {
            return;
        }
        exceptionHandling.defaultAuthenticationEntryPointFor(
                postProcess(authenticationEntryPoint), getAuthenticationEntryPointMatcher(http));
    }

    protected final RequestMatcher getAuthenticationEntryPointMatcher(B http) {
        ContentNegotiationStrategy contentNegotiationStrategy = http
                .getSharedObject(ContentNegotiationStrategy.class);
        if (contentNegotiationStrategy == null) {
            contentNegotiationStrategy = new HeaderContentNegotiationStrategy();
        }

        MediaTypeRequestMatcher mediaMatcher = new MediaTypeRequestMatcher(
                contentNegotiationStrategy, MediaType.APPLICATION_XHTML_XML,
                new MediaType("image", "*"), MediaType.TEXT_HTML, MediaType.TEXT_PLAIN);
        mediaMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));

        RequestMatcher notXRequestedWith = new NegatedRequestMatcher(
                new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"));

        return new AndRequestMatcher(Arrays.asList(notXRequestedWith, mediaMatcher));
    }

    @Override
    public void configure(B http) throws Exception {
        authFilter.setAuthenticationManager(http
                .getSharedObject(AuthenticationManager.class));
        authFilter.setAuthenticationSuccessHandler(successHandler);
        authFilter.setAuthenticationFailureHandler(failureHandler);
        if (authenticationDetailsSource != null) {
            authFilter.setAuthenticationDetailsSource(authenticationDetailsSource);
        }
        SessionAuthenticationStrategy sessionAuthenticationStrategy = http
                .getSharedObject(SessionAuthenticationStrategy.class);
        if (sessionAuthenticationStrategy != null) {
            authFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }
        RememberMeServices rememberMeServices = http
                .getSharedObject(RememberMeServices.class);
        if (rememberMeServices != null) {
            authFilter.setRememberMeServices(rememberMeServices);
        }
        F filter = postProcess(authFilter);
        http.addFilter(filter);
    }

    /**
     * Gets the Authentication Filter
     *
     * @return the Authentication Filter
     */
    protected final F getAuthenticationFilter() {
        return authFilter;
    }

    /**
     * Sets the Authentication Filter
     *
     * @param authFilter the Authentication Filter
     */
    protected final void setAuthenticationFilter(F authFilter) {
        this.authFilter = authFilter;
    }

    /**
     * Gets the Authentication Entry Point
     *
     * @return the Authentication Entry Point
     */
    protected final AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return authenticationEntryPoint;
    }

    /**
     * Gets the URL to submit an authentication request to (i.e. where username/password
     * must be submitted)
     *
     * @return the URL to submit an authentication request to
     */
    protected final String getLoginProcessingUrl() {
        return loginProcessingUrl;
    }

    /**
     * Updates the default values for access.
     */
    protected final void updateAccessDefaults(B http) {
        if (permitAll) {
            PermitAllSupport.permitAll(http, loginProcessingUrl);
        }
    }



    @SuppressWarnings("unchecked")
    private T getSelf() {
        return (T) this;

    }

}