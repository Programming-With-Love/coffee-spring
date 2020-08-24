package org.springframework.security.config.annotation.web.builders;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.AbstractConfiguredSecurityBuilder;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.AbstractRequestMatcherRegistry;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2ClientConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.annotation.web.configurers.openid.OpenIDLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.saml2.Saml2LoginConfigurer;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import site.zido.coffee.security.authentication.phone.PhoneAuthenticationFilter;
import site.zido.coffee.security.configurers.RestExceptionHandlingConfigurer;
import site.zido.coffee.security.configurers.RestFormLoginConfigurer;
import site.zido.coffee.security.configurers.RestSecurityContextConfigurer;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SecurityFilterChain 构建者
 *
 * @author zido
 */
public final class RestHttpSecurity extends
        AbstractConfiguredSecurityBuilder<DefaultSecurityFilterChain, RestHttpSecurity>
        implements SecurityBuilder<DefaultSecurityFilterChain>,
        HttpSecurityBuilder<RestHttpSecurity> {
    private final RequestMatcherConfigurer requestMatcherConfigurer;
    private List<Filter> filters = new ArrayList<>();
    private RequestMatcher requestMatcher = AnyRequestMatcher.INSTANCE;
    private FilterComparator comparator = new FilterComparator();

    @SuppressWarnings("unchecked")
    public RestHttpSecurity(ObjectPostProcessor<Object> objectPostProcessor,
                            AuthenticationManagerBuilder authenticationBuilder,
                            Map<Class<?>, Object> sharedObjects) {
        super(objectPostProcessor);
        comparator.registerBefore(PhoneAuthenticationFilter.class, UsernamePasswordAuthenticationFilter.class);
        Assert.notNull(authenticationBuilder, "authenticationBuilder cannot be null");
        setSharedObject(AuthenticationManagerBuilder.class, authenticationBuilder);
        for (Map.Entry<Class<?>, Object> entry : sharedObjects
                .entrySet()) {
            setSharedObject((Class<Object>) entry.getKey(), entry.getValue());
        }
        ApplicationContext context = (ApplicationContext) sharedObjects
                .get(ApplicationContext.class);
        this.requestMatcherConfigurer = new RequestMatcherConfigurer(context);
    }

    private ApplicationContext getContext() {
        return getSharedObject(ApplicationContext.class);
    }

    /**
     * openId 登录支持
     *
     * @return the {@link OpenIDLoginConfigurer} for further customizations.
     * @throws Exception ex
     */
    public OpenIDLoginConfigurer<RestHttpSecurity> openidLogin() throws Exception {
        return getOrApply(new OpenIDLoginConfigurer<>());
    }

    /**
     * openId 登录支持
     *
     * @return the {@link RestHttpSecurity} for further customizations
     * @throws Exception ex
     */
    public RestHttpSecurity openidLogin(Customizer<OpenIDLoginConfigurer<RestHttpSecurity>> openidLoginCustomizer) throws Exception {
        openidLoginCustomizer.customize(getOrApply(new OpenIDLoginConfigurer<>()));
        return this;
    }

    /**
     * 响应头配置
     *
     * @return the {@link HeadersConfigurer} for further customizations
     * @throws Exception ex
     */
    public HeadersConfigurer<RestHttpSecurity> headers() throws Exception {
        return getOrApply(new HeadersConfigurer<>());
    }

    /**
     * 响应头配置
     *
     * @return the {@link RestHttpSecurity} for further customizations
     * @throws Exception ex
     */
    public RestHttpSecurity headers(Customizer<HeadersConfigurer<RestHttpSecurity>> headersCustomizer) throws Exception {
        headersCustomizer.customize(getOrApply(new HeadersConfigurer<>()));
        return this;
    }

    /**
     * 跨域配置
     *
     * @return the {@link HeadersConfigurer} for further customizations.
     * @throws Exception ex
     */
    public CorsConfigurer<RestHttpSecurity> cors() throws Exception {
        return getOrApply(new CorsConfigurer<>());
    }

    /**
     * 跨域配置
     *
     * @return the {@link RestHttpSecurity} for further customizations
     * @throws Exception ex
     */
    public RestHttpSecurity cors(Customizer<CorsConfigurer<RestHttpSecurity>> corsCustomizer) throws Exception {
        corsCustomizer.customize(getOrApply(new CorsConfigurer<>()));
        return RestHttpSecurity.this;
    }

    /**
     * session安全管理配置
     *
     * @return the {@link RestHttpSecurity} for further customizations
     * @throws Exception ex
     */
    public SessionManagementConfigurer<RestHttpSecurity> sessionManagement() throws Exception {
        return getOrApply(new SessionManagementConfigurer<>());
    }

    /**
     * 配置portMapper
     *
     * @return the {@link PortMapperConfigurer} for further customizations
     * @throws Exception ex
     */
    public PortMapperConfigurer<RestHttpSecurity> portMapper() throws Exception {
        return getOrApply(new PortMapperConfigurer<>());
    }

    /**
     * 配置portMapper
     *
     * @return the {@link RestHttpSecurity} for further customizations
     * @throws Exception ex
     */
    public RestHttpSecurity portMapper(Customizer<PortMapperConfigurer<RestHttpSecurity>> portMapperCustomizer) throws Exception {
        portMapperCustomizer.customize(getOrApply(new PortMapperConfigurer<>()));
        return RestHttpSecurity.this;
    }

    /**
     * 配置基于容器的认证管理
     *
     * @return the {@link JeeConfigurer} for further customizations
     * @throws Exception ex
     */
    public JeeConfigurer<RestHttpSecurity> jee() throws Exception {
        return getOrApply(new JeeConfigurer<>());
    }

    /**
     * 配置基于容器的认证管理
     *
     * @param jeeCustomizer the {@link Customizer} to provide more options for
     *                      the {@link JeeConfigurer}
     * @return the {@link HttpSecurity} for further customizations
     * @throws Exception ex
     */
    public RestHttpSecurity jee(Customizer<JeeConfigurer<RestHttpSecurity>> jeeCustomizer) throws Exception {
        jeeCustomizer.customize(getOrApply(new JeeConfigurer<>()));
        return RestHttpSecurity.this;
    }


    /**
     * 配置基于x509的认证管理
     *
     * @return the {@link X509Configurer} for further customizations
     * @throws Exception ex
     */
    public X509Configurer<RestHttpSecurity> x509() throws Exception {
        return getOrApply(new X509Configurer<>());
    }

    /**
     * 配置基于x509的认证管理
     *
     * @return the {@link RestHttpSecurity} for further customizations
     * @throws Exception ex
     */
    public RestHttpSecurity x509(Customizer<X509Configurer<RestHttpSecurity>> x509Customizer) throws Exception {
        x509Customizer.customize(getOrApply(new X509Configurer<>()));
        return RestHttpSecurity.this;
    }

    /**
     * 配置remember me功能
     * <p>
     * 在restful情况下不要使用，因为remember me一般基于cookie/session
     *
     * @return the {@link RememberMeConfigurer} for further customizations
     * @throws Exception ex
     */
    public RememberMeConfigurer<RestHttpSecurity> rememberMe() throws Exception {
        return getOrApply(new RememberMeConfigurer<>());
    }

    /**
     * 配置remember me功能
     * <p>
     * 在restful情况下不要使用，因为remember me一般基于cookie/session
     *
     * @return the {@link RememberMeConfigurer} for further customizations
     * @throws Exception ex
     */
    public RestHttpSecurity rememberMe(Customizer<RememberMeConfigurer<RestHttpSecurity>> rememberMeCustomizer) throws Exception {
        rememberMeCustomizer.customize(getOrApply(new RememberMeConfigurer<>()));
        return this;
    }

    /**
     * Allows restricting access based upon the {@link HttpServletRequest} using
     *
     * <h2>Example Configurations</h2>
     * <p>
     * The most basic example is to configure all URLs to require the role "ROLE_USER".
     * The configuration below requires authentication to every URL and will grant access
     * to both the user "admin" and "user".
     *
     * <pre>
     * &#064;Configuration
     * &#064;EnableWebSecurity
     * public class AuthorizeUrlsSecurityConfig extends WebSecurityConfigurerAdapter {
     *
     * 	&#064;Override
     * 	protected void configure(RestHttpSecurity http) throws Exception {
     * 		http.authorizeRequests().antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;).and().formLogin();
     *    }
     *
     * 	&#064;Override
     * 	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
     * 		auth.inMemoryAuthentication().withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;)
     * 				.and().withUser(&quot;admin&quot;).password(&quot;password&quot;).roles(&quot;ADMIN&quot;, &quot;USER&quot;);
     *    }
     * }
     * </pre>
     * <p>
     * We can also configure multiple URLs. The configuration below requires
     * authentication to every URL and will grant access to URLs starting with /admin/ to
     * only the "admin" user. All other URLs either user can access.
     *
     * <pre>
     * &#064;Configuration
     * &#064;EnableWebSecurity
     * public class AuthorizeUrlsSecurityConfig extends WebSecurityConfigurerAdapter {
     *
     * 	&#064;Override
     * 	protected void configure(RestHttpSecurity http) throws Exception {
     * 		http.authorizeRequests().antMatchers(&quot;/admin/**&quot;).hasRole(&quot;ADMIN&quot;)
     * 				.antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;).and().formLogin();
     *    }
     *
     * 	&#064;Override
     * 	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
     * 		auth.inMemoryAuthentication().withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;)
     * 				.and().withUser(&quot;admin&quot;).password(&quot;password&quot;).roles(&quot;ADMIN&quot;, &quot;USER&quot;);
     *    }
     * }
     * </pre>
     * <p>
     * Note that the matchers are considered in order. Therefore, the following is invalid
     * because the first matcher matches every request and will never get to the second
     * mapping:
     *
     * <pre>
     * http.authorizeRequests().antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;).antMatchers(&quot;/admin/**&quot;)
     * 		.hasRole(&quot;ADMIN&quot;)
     * </pre>
     *
     * @return the {@link ExpressionUrlAuthorizationConfigurer} for further customizations
     * @throws Exception ex
     * @see #requestMatcher(RequestMatcher)
     */
    public ExpressionUrlAuthorizationConfigurer<RestHttpSecurity>.ExpressionInterceptUrlRegistry authorizeRequests()
            throws Exception {
        ApplicationContext context = getContext();
        return getOrApply(new ExpressionUrlAuthorizationConfigurer<>(context))
                .getRegistry();
    }

    /**
     * Allows restricting access based upon the {@link HttpServletRequest} using
     * {@link RequestMatcher} implementations (i.e. via URL patterns).
     *
     * <h2>Example Configurations</h2>
     * <p>
     * The most basic example is to configure all URLs to require the role "ROLE_USER".
     * The configuration below requires authentication to every URL and will grant access
     * to both the user "admin" and "user".
     *
     * <pre>
     * &#064;Configuration
     * &#064;EnableWebSecurity
     * public class AuthorizeUrlsSecurityConfig extends WebSecurityConfigurerAdapter {
     *
     * 	&#064;Override
     * 	protected void configure(RestHttpSecurity http) throws Exception {
     * 		http
     * 			.authorizeRequests(authorizeRequests ->
     * 				authorizeRequests
     * 					.antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;)
     * 			)
     * 			.formLogin(withDefaults());
     *    }
     * }
     * </pre>
     * <p>
     * We can also configure multiple URLs. The configuration below requires
     * authentication to every URL and will grant access to URLs starting with /admin/ to
     * only the "admin" user. All other URLs either user can access.
     *
     * <pre>
     * &#064;Configuration
     * &#064;EnableWebSecurity
     * public class AuthorizeUrlsSecurityConfig extends WebSecurityConfigurerAdapter {
     *
     * 	&#064;Override
     * 	protected void configure(RestHttpSecurity http) throws Exception {
     * 		http
     * 			.authorizeRequests(authorizeRequests ->
     * 				authorizeRequests
     * 					.antMatchers(&quot;/admin/**&quot;).hasRole(&quot;ADMIN&quot;)
     * 					.antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;)
     * 			)
     * 			.formLogin(withDefaults());
     *    }
     * }
     * </pre>
     * <p>
     * Note that the matchers are considered in order. Therefore, the following is invalid
     * because the first matcher matches every request and will never get to the second
     * mapping:
     *
     * <pre>
     * &#064;Configuration
     * &#064;EnableWebSecurity
     * public class AuthorizeUrlsSecurityConfig extends WebSecurityConfigurerAdapter {
     *
     * 	&#064;Override
     * 	protected void configure(RestHttpSecurity http) throws Exception {
     * 		 http
     * 		 	.authorizeRequests(authorizeRequests ->
     * 		 		authorizeRequests
     * 			 		.antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;)
     * 			 		.antMatchers(&quot;/admin/**&quot;).hasRole(&quot;ADMIN&quot;)
     * 		 	);
     *    }
     * }
     * </pre>
     *
     * @param authorizeRequestsCustomizer the {@link Customizer} to provide more options for
     *                                    the {@link ExpressionUrlAuthorizationConfigurer.ExpressionInterceptUrlRegistry}
     * @return the {@link RestHttpSecurity} for further customizations
     * @throws Exception ex
     * @see #requestMatcher(RequestMatcher)
     */
    public RestHttpSecurity authorizeRequests(Customizer<ExpressionUrlAuthorizationConfigurer<RestHttpSecurity>.ExpressionInterceptUrlRegistry> authorizeRequestsCustomizer)
            throws Exception {
        ApplicationContext context = getContext();
        authorizeRequestsCustomizer.customize(getOrApply(new ExpressionUrlAuthorizationConfigurer<>(context))
                .getRegistry());
        return RestHttpSecurity.this;
    }

    /**
     * Allows configuring the Request Cache. For example, a protected page (/protected)
     * may be requested prior to authentication. The application will redirect the user to
     * a login page. After authentication, Spring Security will redirect the user to the
     * originally requested protected page (/protected). This is automatically applied
     * when using {@link WebSecurityConfigurerAdapter}.
     * <p>
     * restful情况下不要使用
     *
     * @return the {@link RequestCacheConfigurer} for further customizations
     * @throws Exception ex
     */
    public RequestCacheConfigurer<RestHttpSecurity> requestCache() throws Exception {
        return getOrApply(new RequestCacheConfigurer<>());
    }

    /**
     * Allows configuring the Request Cache. For example, a protected page (/protected)
     * may be requested prior to authentication. The application will redirect the user to
     * a login page. After authentication, Spring Security will redirect the user to the
     * originally requested protected page (/protected). This is automatically applied
     * when using {@link WebSecurityConfigurerAdapter}.
     *
     * <h2>Example Custom Configuration</h2>
     * <p>
     * The following example demonstrates how to disable request caching.
     *
     * <pre>
     * &#064;Configuration
     * &#064;EnableWebSecurity
     * public class RequestCacheDisabledSecurityConfig extends WebSecurityConfigurerAdapter {
     *
     * 	&#064;Override
     * 	protected void configure(HttpSecurity http) throws Exception {
     * 		http
     * 			.authorizeRequests(authorizeRequests ->
     * 				authorizeRequests
     * 					.antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;)
     * 			)
     * 			.requestCache(requestCache ->
     * 				requestCache.disable()
     * 			);
     *    }
     * }
     * </pre>
     * <p>
     * restful情况下不要使用
     *
     * @param requestCacheCustomizer the {@link Customizer} to provide more options for
     *                               the {@link RequestCacheConfigurer}
     * @return the {@link HttpSecurity} for further customizations
     * @throws Exception ex
     */
    public RestHttpSecurity requestCache(Customizer<RequestCacheConfigurer<RestHttpSecurity>> requestCacheCustomizer)
            throws Exception {
        requestCacheCustomizer.customize(getOrApply(new RequestCacheConfigurer<>()));
        return this;
    }

    /**
     * Allows configuring exception handling. This is automatically applied when using
     * {@link WebSecurityConfigurerAdapter}.
     *
     * @return the {@link ExceptionHandlingConfigurer} for further customizations
     * @throws Exception ex
     */
    public RestExceptionHandlingConfigurer<RestHttpSecurity> exceptionHandling() throws Exception {
        return getOrApply(new RestExceptionHandlingConfigurer<>());
    }

    /**
     * Allows configuring exception handling. This is automatically applied when using
     * {@link WebSecurityConfigurerAdapter}.
     *
     * <h2>Example Custom Configuration</h2>
     * <p>
     * The following customization will ensure that users who are denied access are forwarded
     * to the page "/errors/access-denied".
     *
     * <pre>
     * &#064;Configuration
     * &#064;EnableWebSecurity
     * public class ExceptionHandlingSecurityConfig extends WebSecurityConfigurerAdapter {
     *
     * 	&#064;Override
     * 	protected void configure(RestHttpSecurity http) throws Exception {
     * 		http
     * 			.authorizeRequests(authorizeRequests ->
     * 				authorizeRequests
     * 					.antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;)
     * 			)
     * 			// sample exception handling customization
     * 			.exceptionHandling(exceptionHandling ->
     * 				exceptionHandling
     * 					.accessDeniedPage(&quot;/errors/access-denied&quot;)
     * 			);
     *    }
     * }
     * </pre>
     *
     * @param exceptionHandlingCustomizer the {@link Customizer} to provide more options for
     *                                    the {@link ExceptionHandlingConfigurer}
     * @return the {@link RestHttpSecurity} for further customizations
     * @throws Exception ex
     */
    public RestHttpSecurity exceptionHandling(Customizer<RestExceptionHandlingConfigurer<RestHttpSecurity>> exceptionHandlingCustomizer) throws Exception {
        exceptionHandlingCustomizer.customize(getOrApply(new RestExceptionHandlingConfigurer<>()));
        return RestHttpSecurity.this;
    }

    /**
     * Sets up management of the {@link SecurityContext} on the
     * {@link SecurityContextHolder} between {@link HttpServletRequest}'s. This is
     * automatically applied when using {@link WebSecurityConfigurerAdapter}.
     *
     * @return the {@link SecurityContextConfigurer} for further customizations
     * @throws Exception ex
     */
    public RestSecurityContextConfigurer<RestHttpSecurity> securityContext() throws Exception {
        return getOrApply(new RestSecurityContextConfigurer<>());
    }

    /**
     * Sets up management of the {@link SecurityContext} on the
     * {@link SecurityContextHolder} between {@link HttpServletRequest}'s. This is
     * automatically applied when using {@link WebSecurityConfigurerAdapter}.
     * <p>
     * The following customization specifies the shared {@link SecurityContextRepository}
     *
     * <pre>
     * &#064;Configuration
     * &#064;EnableWebSecurity
     * public class SecurityContextSecurityConfig extends WebSecurityConfigurerAdapter {
     *
     * 	&#064;Override
     * 	protected void configure(RestHttpSecurity http) throws Exception {
     * 		http
     * 			.securityContext(securityContext ->
     * 				securityContext
     * 					.securityContextRepository(SCR)
     * 			);
     *    }
     * }
     * </pre>
     *
     * @param securityContextCustomizer the {@link Customizer} to provide more options for
     *                                  the {@link SecurityContextConfigurer}
     * @return the {@link RestHttpSecurity} for further customizations
     * @throws Exception ex
     */
    public RestHttpSecurity securityContext(Customizer<RestSecurityContextConfigurer<RestHttpSecurity>> securityContextCustomizer) throws Exception {
        securityContextCustomizer.customize(getOrApply(new RestSecurityContextConfigurer<>()));
        return RestHttpSecurity.this;
    }

    /**
     * Integrates the {@link HttpServletRequest} methods with the values found on the
     * {@link SecurityContext}. This is automatically applied when using
     * {@link WebSecurityConfigurerAdapter}.
     *
     * @return the {@link ServletApiConfigurer} for further customizations
     * @throws Exception ex
     */
    public ServletApiConfigurer<RestHttpSecurity> servletApi() throws Exception {
        return getOrApply(new ServletApiConfigurer<>());
    }

    /**
     * Integrates the {@link HttpServletRequest} methods with the values found on the
     * {@link SecurityContext}. This is automatically applied when using
     * {@link WebSecurityConfigurerAdapter}. You can disable it using:
     *
     * <pre>
     * &#064;Configuration
     * &#064;EnableWebSecurity
     * public class ServletApiSecurityConfig extends WebSecurityConfigurerAdapter {
     *
     * 	&#064;Override
     * 	protected void configure(RestHttpSecurity http) throws Exception {
     * 		http
     * 			.servletApi(servletApi ->
     * 				servletApi.disable()
     * 			);
     *    }
     * }
     * </pre>
     *
     * @param servletApiCustomizer the {@link Customizer} to provide more options for
     *                             the {@link ServletApiConfigurer}
     * @return the {@link RestHttpSecurity} for further customizations
     * @throws Exception ex
     */
    public RestHttpSecurity servletApi(Customizer<ServletApiConfigurer<RestHttpSecurity>> servletApiCustomizer) throws Exception {
        servletApiCustomizer.customize(getOrApply(new ServletApiConfigurer<>()));
        return RestHttpSecurity.this;
    }

    /**
     * Adds CSRF support. This is activated by default when using
     * {@link WebSecurityConfigurerAdapter}'s default constructor. You can disable it
     * using:
     *
     * <pre>
     * &#064;Configuration
     * &#064;EnableWebSecurity
     * public class CsrfSecurityConfig extends WebSecurityConfigurerAdapter {
     *
     * 	&#064;Override
     *     protected void configure(HttpSecurity http) throws Exception {
     *         http
     *             .csrf().disable()
     *             ...;
     *     }
     * }
     * </pre>
     * <p>
     * restful情况下不要使用
     *
     * @return the {@link ServletApiConfigurer} for further customizations
     * @throws Exception ex
     */
    public CsrfConfigurer<RestHttpSecurity> csrf() throws Exception {
        ApplicationContext context = getContext();
        return getOrApply(new CsrfConfigurer<>(context));
    }

    /**
     * Adds CSRF support. This is activated by default when using
     * {@link WebSecurityConfigurerAdapter}'s default constructor. You can disable it
     * using:
     *
     * <pre>
     * &#064;Configuration
     * &#064;EnableWebSecurity
     * public class CsrfSecurityConfig extends WebSecurityConfigurerAdapter {
     *
     * 	&#064;Override
     *     protected void configure(HttpSecurity http) throws Exception {
     *         http
     *             .csrf(csrf -> csrf.disable());
     *     }
     * }
     * </pre>
     * <p>
     * restful情况下不要使用
     *
     * @param csrfCustomizer the {@link Customizer} to provide more options for
     *                       the {@link CsrfConfigurer}
     * @return the {@link HttpSecurity} for further customizations
     * @throws Exception ex
     */
    public RestHttpSecurity csrf(Customizer<CsrfConfigurer<RestHttpSecurity>> csrfCustomizer) throws Exception {
        ApplicationContext context = getContext();
        csrfCustomizer.customize(getOrApply(new CsrfConfigurer<>(context)));
        return this;
    }

    /**
     * Provides logout support. This is automatically applied when using
     * {@link WebSecurityConfigurerAdapter}. The default is that accessing the URL
     * "/logout" will log the user out by invalidating the HTTP Session, cleaning up any
     * {@link #rememberMe()} authentication that was configured, clearing the
     * {@link SecurityContextHolder}, and then redirect to "/login?success".
     *
     * <h2>Example Custom Configuration</h2>
     * <p>
     * The following customization to log out when the URL "/custom-logout" is invoked.
     * Log out will remove the cookie named "remove", not invalidate the HttpSession,
     * clear the SecurityContextHolder, and upon completion redirect to "/logout-success".
     *
     * <pre>
     * &#064;Configuration
     * &#064;EnableWebSecurity
     * public class LogoutSecurityConfig extends WebSecurityConfigurerAdapter {
     *
     * 	&#064;Override
     * 	protected void configure(RestHttpSecurity http) throws Exception {
     * 		http.authorizeRequests().antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;).and().formLogin()
     * 				.and()
     * 				// sample logout customization
     * 				.logout().deleteCookies(&quot;remove&quot;).invalidateHttpSession(false)
     * 				.logoutUrl(&quot;/custom-logout&quot;).logoutSuccessUrl(&quot;/logout-success&quot;);
     *    }
     *
     * 	&#064;Override
     * 	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
     * 		auth.inMemoryAuthentication().withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;);
     *    }
     * }
     * </pre>
     *
     * @return the {@link LogoutConfigurer} for further customizations
     * @throws Exception ex
     */
    public RestLogoutConfigurer<RestHttpSecurity> logout() throws Exception {
        return getOrApply(new RestLogoutConfigurer<>());
    }

    /**
     * Provides logout support. This is automatically applied when using
     * {@link WebSecurityConfigurerAdapter}. The default is that accessing the URL
     * "/logout" will log the user out by invalidating the HTTP Session, cleaning up any
     * {@link #rememberMe()} authentication that was configured, clearing the
     * {@link SecurityContextHolder}, and then redirect to "/login?success".
     *
     * <h2>Example Custom Configuration</h2>
     * <p>
     * The following customization to log out when the URL "/custom-logout" is invoked.
     * Log out will remove the cookie named "remove", not invalidate the HttpSession,
     * clear the SecurityContextHolder, and upon completion redirect to "/logout-success".
     *
     * <pre>
     * &#064;Configuration
     * &#064;EnableWebSecurity
     * public class LogoutSecurityConfig extends WebSecurityConfigurerAdapter {
     *
     * 	&#064;Override
     * 	protected void configure(RestHttpSecurity http) throws Exception {
     * 		http
     * 			.authorizeRequests(authorizeRequests ->
     * 				authorizeRequests
     * 					.antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;)
     * 			)
     * 			.formLogin(withDefaults())
     * 			// sample logout customization
     * 			.logout(logout ->
     * 				logout.deleteCookies(&quot;remove&quot;)
     * 					.invalidateHttpSession(false)
     * 					.logoutUrl(&quot;/custom-logout&quot;)
     * 					.logoutSuccessUrl(&quot;/logout-success&quot;)
     * 			);
     *    }
     * }
     * </pre>
     *
     * @param logoutCustomizer the {@link Customizer} to provide more options for
     *                         the {@link LogoutConfigurer}
     * @return the {@link RestHttpSecurity} for further customizations
     * @throws Exception ex
     */
    public RestHttpSecurity logout(Customizer<RestLogoutConfigurer<RestHttpSecurity>> logoutCustomizer) throws Exception {
        logoutCustomizer.customize(getOrApply(new RestLogoutConfigurer<>()));
        return this;
    }

    public AnonymousConfigurer<RestHttpSecurity> anonymous() throws Exception {
        return getOrApply(new AnonymousConfigurer<>());
    }

    public RestHttpSecurity anonymous(Customizer<AnonymousConfigurer<RestHttpSecurity>> anonymousCustomizer) throws Exception {
        anonymousCustomizer.customize(getOrApply(new AnonymousConfigurer<>()));
        return this;
    }

    public RestFormLoginConfigurer<RestHttpSecurity> formLogin() throws Exception {
        return getOrApply(new RestFormLoginConfigurer<>());
    }

    public RestHttpSecurity formLogin(Customizer<RestFormLoginConfigurer<RestHttpSecurity>> formLoginCustomizer) throws Exception {
        formLoginCustomizer.customize(getOrApply(new RestFormLoginConfigurer<>()));
        return RestHttpSecurity.this;
    }

    public PhoneCodeLoginConfigurer<RestHttpSecurity> phoneCodeLogin() throws Exception {
        return getOrApply(new PhoneCodeLoginConfigurer<>());
    }

    public RestHttpSecurity phoneCodeLogin(Customizer<PhoneCodeLoginConfigurer<RestHttpSecurity>> phoneCodeLoginCustomizer) throws Exception {
        phoneCodeLoginCustomizer.customize(getOrApply(new PhoneCodeLoginConfigurer<>()));
        return RestHttpSecurity.this;
    }

    public Saml2LoginConfigurer<RestHttpSecurity> saml2Login() throws Exception {
        return getOrApply(new Saml2LoginConfigurer<>());
    }

    public RestHttpSecurity saml2Login(Customizer<Saml2LoginConfigurer<RestHttpSecurity>> saml2LoginCustomizer) throws Exception {
        saml2LoginCustomizer.customize(getOrApply(new Saml2LoginConfigurer<>()));
        return RestHttpSecurity.this;
    }

    public OAuth2LoginConfigurer<RestHttpSecurity> oauth2Login() throws Exception {
        return getOrApply(new OAuth2LoginConfigurer<>());
    }

    public RestHttpSecurity oauth2Login(Customizer<OAuth2LoginConfigurer<RestHttpSecurity>> oauth2LoginCustomizer) throws Exception {
        oauth2LoginCustomizer.customize(getOrApply(new OAuth2LoginConfigurer<>()));
        return this;
    }

    public OAuth2ClientConfigurer<RestHttpSecurity> oauth2Client() throws Exception {
        OAuth2ClientConfigurer<RestHttpSecurity> configurer = getOrApply(new OAuth2ClientConfigurer<>());
        this.postProcess(configurer);
        return configurer;
    }

    public RestHttpSecurity oauth2Client(Customizer<OAuth2ClientConfigurer<RestHttpSecurity>> oauth2ClientCustomizer) throws Exception {
        oauth2ClientCustomizer.customize(getOrApply(new OAuth2ClientConfigurer<>()));
        return RestHttpSecurity.this;
    }

    public OAuth2ResourceServerConfigurer<RestHttpSecurity> oauth2ResourceServer() throws Exception {
        OAuth2ResourceServerConfigurer<RestHttpSecurity> configurer = getOrApply(new OAuth2ResourceServerConfigurer<>(getContext()));
        this.postProcess(configurer);
        return configurer;
    }

    public RestHttpSecurity oauth2ResourceServer(Customizer<OAuth2ResourceServerConfigurer<RestHttpSecurity>> oauth2ResourceServerCustomizer)
            throws Exception {
        OAuth2ResourceServerConfigurer<RestHttpSecurity> configurer = getOrApply(new OAuth2ResourceServerConfigurer<>(getContext()));
        this.postProcess(configurer);
        oauth2ResourceServerCustomizer.customize(configurer);
        return RestHttpSecurity.this;
    }

    public ChannelSecurityConfigurer<RestHttpSecurity>.ChannelRequestMatcherRegistry requiresChannel()
            throws Exception {
        ApplicationContext context = getContext();
        return getOrApply(new ChannelSecurityConfigurer<>(context))
                .getRegistry();
    }

    public RestHttpSecurity requiresChannel(Customizer<ChannelSecurityConfigurer<RestHttpSecurity>.ChannelRequestMatcherRegistry> requiresChannelCustomizer)
            throws Exception {
        ApplicationContext context = getContext();
        requiresChannelCustomizer.customize(getOrApply(new ChannelSecurityConfigurer<>(context))
                .getRegistry());
        return RestHttpSecurity.this;
    }

    public HttpBasicConfigurer<RestHttpSecurity> httpBasic() throws Exception {
        return getOrApply(new HttpBasicConfigurer<>());
    }

    public RestHttpSecurity httpBasic(Customizer<HttpBasicConfigurer<RestHttpSecurity>> httpBasicCustomizer) throws Exception {
        httpBasicCustomizer.customize(getOrApply(new HttpBasicConfigurer<>()));
        return RestHttpSecurity.this;
    }

    @Override
    public <C> void setSharedObject(Class<C> sharedType, C object) {
        super.setSharedObject(sharedType, object);
    }

    @Override
    protected void beforeConfigure() throws Exception {
        setSharedObject(AuthenticationManager.class, getAuthenticationRegistry().build());
    }

    @Override
    protected DefaultSecurityFilterChain performBuild() {
        filters.sort(comparator);
        return new DefaultSecurityFilterChain(requestMatcher, filters);
    }

    @Override
    public RestHttpSecurity authenticationProvider(
            AuthenticationProvider authenticationProvider) {
        getAuthenticationRegistry().authenticationProvider(authenticationProvider);
        return this;
    }

    @Override
    public RestHttpSecurity userDetailsService(UserDetailsService userDetailsService)
            throws Exception {
        getAuthenticationRegistry().userDetailsService(userDetailsService);
        return this;
    }

    private AuthenticationManagerBuilder getAuthenticationRegistry() {
        return getSharedObject(AuthenticationManagerBuilder.class);
    }

    @Override
    public RestHttpSecurity addFilterAfter(Filter filter, Class<? extends Filter> afterFilter) {
        comparator.registerAfter(filter.getClass(), afterFilter);
        return addFilter(filter);
    }

    @Override
    public RestHttpSecurity addFilterBefore(Filter filter,
                                            Class<? extends Filter> beforeFilter) {
        comparator.registerBefore(filter.getClass(), beforeFilter);
        return addFilter(filter);
    }

    @Override
    public RestHttpSecurity addFilter(Filter filter) {
        Class<? extends Filter> filterClass = filter.getClass();
        if (!comparator.isRegistered(filterClass)) {
            throw new IllegalArgumentException(
                    "The Filter class "
                            + filterClass.getName()
                            + " does not have a registered order and cannot be added without a specified order. Consider using addFilterBefore or addFilterAfter instead.");
        }
        this.filters.add(filter);
        return this;
    }

    public RestHttpSecurity addFilterAt(Filter filter, Class<? extends Filter> atFilter) {
        this.comparator.registerAt(filter.getClass(), atFilter);
        return addFilter(filter);
    }

    public RequestMatcherConfigurer requestMatchers() {
        return requestMatcherConfigurer;
    }

    public RestHttpSecurity requestMatchers(Customizer<RequestMatcherConfigurer> requestMatcherCustomizer) {
        requestMatcherCustomizer.customize(requestMatcherConfigurer);
        return RestHttpSecurity.this;
    }

    public RestHttpSecurity requestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
        return this;
    }


    public RestHttpSecurity mvcMatcher(String mvcPattern) {
        HandlerMappingIntrospector introspector = new HandlerMappingIntrospector();
        introspector.setApplicationContext(getContext());
        return requestMatcher(new MvcRequestMatcher(introspector, mvcPattern));
    }

    public RestHttpSecurity regexMatcher(String pattern) {
        return requestMatcher(new RegexRequestMatcher(pattern, null));
    }

    public final class MvcMatchersRequestMatcherConfigurer extends RequestMatcherConfigurer {

        private MvcMatchersRequestMatcherConfigurer(ApplicationContext context,
                                                    List<MvcRequestMatcher> matchers) {
            super(context);
            this.matchers = new ArrayList<>(matchers);
        }

        public RequestMatcherConfigurer servletPath(String servletPath) {
            for (RequestMatcher matcher : this.matchers) {
                ((MvcRequestMatcher) matcher).setServletPath(servletPath);
            }
            return this;
        }

    }

    public class RequestMatcherConfigurer
            extends AbstractRequestMatcherRegistry<RequestMatcherConfigurer> {

        protected List<RequestMatcher> matchers = new ArrayList<>();

        private RequestMatcherConfigurer(ApplicationContext context) {
            setApplicationContext(context);
        }

        @Override
        public MvcMatchersRequestMatcherConfigurer mvcMatchers(HttpMethod method,
                                                               String... mvcPatterns) {
            List<MvcRequestMatcher> mvcMatchers = createMvcMatchers(method, mvcPatterns);
            setMatchers(mvcMatchers);
            return new MvcMatchersRequestMatcherConfigurer(getContext(), mvcMatchers);
        }

        @Override
        public MvcMatchersRequestMatcherConfigurer mvcMatchers(String... patterns) {
            return mvcMatchers(null, patterns);
        }

        @Override
        protected RequestMatcherConfigurer chainRequestMatchers(
                List<RequestMatcher> requestMatchers) {
            setMatchers(requestMatchers);
            return this;
        }

        private void setMatchers(List<? extends RequestMatcher> requestMatchers) {
            this.matchers.addAll(requestMatchers);
            requestMatcher(new OrRequestMatcher(this.matchers));
        }

        public RestHttpSecurity and() {
            return RestHttpSecurity.this;
        }

    }

    @SuppressWarnings("unchecked")
    private <C extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, RestHttpSecurity>> C getOrApply(
            C configurer) throws Exception {
        C existingConfig = (C) getConfigurer(configurer.getClass());
        if (existingConfig != null) {
            return existingConfig;
        }
        return apply(configurer);
    }
}
