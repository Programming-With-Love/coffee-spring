package site.zido.coffee.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.RestHttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import site.zido.coffee.security.authentication.phone.PhoneCodeService;
import site.zido.coffee.security.configurers.RestAccessDeniedHandlerImpl;

import java.lang.reflect.Field;
import java.util.*;

/**
 * rest 风格安全配置适配器
 *
 * @author zido
 */
public class RestSecurityConfigurationAdapter implements WebSecurityConfigurer<WebSecurity> {
    private final Logger logger = LoggerFactory.getLogger(RestSecurityConfigurationAdapter.class);
    private ApplicationContext context;

    private ContentNegotiationStrategy contentNegotiationStrategy = new HeaderContentNegotiationStrategy();

    private ObjectPostProcessor<Object> objectPostProcessor = new ObjectPostProcessor<Object>() {
        @Override
        public <T> T postProcess(T object) {
            throw new IllegalStateException(
                    ObjectPostProcessor.class.getName()
                            + " is a required bean. Ensure you have used @EnableWebSecurity and @Configuration");
        }
    };
    private AuthenticationConfiguration authenticationConfiguration;
    private AuthenticationManagerBuilder authenticationBuilder;
    private AuthenticationManagerBuilder localConfigureAuthenticationBldr;
    private boolean disableLocalConfigureAuthenticationBldr;
    private boolean authenticationManagerInitialized;
    private AuthenticationManager authenticationManager;
    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
    private RestHttpSecurity http;
    private final boolean disableDefaults;

    protected RestSecurityConfigurationAdapter() {
        this(false);
    }

    protected RestSecurityConfigurationAdapter(boolean disableDefaults) {
        this.disableDefaults = disableDefaults;
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        this.disableLocalConfigureAuthenticationBldr = true;
    }

    @SuppressWarnings("unchecked")
    protected final RestHttpSecurity getHttp() throws Exception {
        if (http != null) {
            return http;
        }

        DefaultAuthenticationEventPublisher eventPublisher = objectPostProcessor
                .postProcess(new DefaultAuthenticationEventPublisher());
        localConfigureAuthenticationBldr.authenticationEventPublisher(eventPublisher);

        AuthenticationManager authenticationManager = authenticationManager();
        authenticationBuilder.parentAuthenticationManager(authenticationManager);
        authenticationBuilder.authenticationEventPublisher(eventPublisher);
        Map<Class<?>, Object> sharedObjects = createSharedObjects();

        http = new RestHttpSecurity(objectPostProcessor, authenticationBuilder,
                sharedObjects);
        if (!disableDefaults) {
            http
                    .addFilter(new WebAsyncManagerIntegrationFilter())
                    .exceptionHandling().accessDeniedHandler(new RestAccessDeniedHandlerImpl()).and()
                    .headers().and()
                    .securityContext().and()
                    .servletApi().and()
                    .anonymous().and()
                    .logout();
            ClassLoader classLoader = this.context.getClassLoader();
            List<AbstractHttpConfigurer> defaultHttpConfigurers =
                    SpringFactoriesLoader.loadFactories(AbstractHttpConfigurer.class, classLoader);

            for (AbstractHttpConfigurer configurer : defaultHttpConfigurers) {
                http.apply(configurer);
            }
        }
        configure(http);
        return http;
    }

    public AuthenticationManager authenticationManagerBean() throws Exception {
        return new AuthenticationManagerDelegator(authenticationBuilder, context);
    }

    protected AuthenticationManager authenticationManager() throws Exception {
        if (!authenticationManagerInitialized) {
            configure(localConfigureAuthenticationBldr);
            if (disableLocalConfigureAuthenticationBldr) {
                authenticationManager = authenticationConfiguration
                        .getAuthenticationManager();
            } else {
                authenticationManager = localConfigureAuthenticationBldr.build();
            }
            authenticationManagerInitialized = true;
        }
        return authenticationManager;
    }

    public UserDetailsService userDetailsServiceBean() throws Exception {
        AuthenticationManagerBuilder globalAuthBuilder = context
                .getBean(AuthenticationManagerBuilder.class);
        return new UserDetailsServiceDelegator(Arrays.asList(
                localConfigureAuthenticationBldr, globalAuthBuilder));
    }

    protected UserDetailsService userDetailsService() {
        AuthenticationManagerBuilder globalAuthBuilder = context
                .getBean(AuthenticationManagerBuilder.class);
        return new UserDetailsServiceDelegator(Arrays.asList(
                localConfigureAuthenticationBldr, globalAuthBuilder));
    }

    protected PhoneCodeService phoneCodeService() {
        return null;
    }

    @Override
    public void init(final WebSecurity web) throws Exception {
        final RestHttpSecurity http = getHttp();
        web.addSecurityFilterChainBuilder(http).postBuildAction(() -> {
            FilterSecurityInterceptor securityInterceptor = http
                    .getSharedObject(FilterSecurityInterceptor.class);
            web.securityInterceptor(securityInterceptor);
        });
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
    }

    protected void configure(RestHttpSecurity http) throws Exception {
        logger.debug("Using default configure(HttpSecurity). If subclassed this will potentially override subclass configure(HttpSecurity).");

        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin().and()
                .httpBasic();
    }

    protected final ApplicationContext getApplicationContext() {
        return this.context;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;

        ObjectPostProcessor<Object> objectPostProcessor = context.getBean(ObjectPostProcessor.class);
        LazyPasswordEncoder passwordEncoder = new LazyPasswordEncoder(context);

        authenticationBuilder = new DefaultPasswordEncoderAuthenticationManagerBuilder(objectPostProcessor, passwordEncoder);
        localConfigureAuthenticationBldr = new DefaultPasswordEncoderAuthenticationManagerBuilder(objectPostProcessor, passwordEncoder) {
            @Override
            public AuthenticationManagerBuilder eraseCredentials(boolean eraseCredentials) {
                authenticationBuilder.eraseCredentials(eraseCredentials);
                return super.eraseCredentials(eraseCredentials);
            }

        };
    }

    @Autowired(required = false)
    public void setTrustResolver(AuthenticationTrustResolver trustResolver) {
        this.trustResolver = trustResolver;
    }

    @Autowired(required = false)
    public void setContentNegotiationStrategy(
            ContentNegotiationStrategy contentNegotiationStrategy) {
        this.contentNegotiationStrategy = contentNegotiationStrategy;
    }

    @Autowired
    public void setObjectPostProcessor(ObjectPostProcessor<Object> objectPostProcessor) {
        this.objectPostProcessor = objectPostProcessor;
    }

    @Autowired
    public void setAuthenticationConfiguration(
            AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }

    private Map<Class<?>, Object> createSharedObjects() {
        Map<Class<?>, Object> sharedObjects =
                new HashMap<>(localConfigureAuthenticationBldr.getSharedObjects());
        sharedObjects.put(UserDetailsService.class, userDetailsService());
        sharedObjects.put(PhoneCodeService.class, phoneCodeService());
        sharedObjects.put(ApplicationContext.class, context);
        sharedObjects.put(ContentNegotiationStrategy.class, contentNegotiationStrategy);
        sharedObjects.put(AuthenticationTrustResolver.class, trustResolver);
        return sharedObjects;
    }

    static final class UserDetailsServiceDelegator implements UserDetailsService {
        private List<AuthenticationManagerBuilder> delegateBuilders;
        private UserDetailsService delegate;
        private final Object delegateMonitor = new Object();

        UserDetailsServiceDelegator(List<AuthenticationManagerBuilder> delegateBuilders) {
            if (delegateBuilders.contains(null)) {
                throw new IllegalArgumentException(
                        "delegateBuilders cannot contain null values. Got "
                                + delegateBuilders);
            }
            this.delegateBuilders = delegateBuilders;
        }

        @Override
        public UserDetails loadUserByUsername(String username)
                throws UsernameNotFoundException {
            if (delegate != null) {
                return delegate.loadUserByUsername(username);
            }

            synchronized (delegateMonitor) {
                if (delegate == null) {
                    for (AuthenticationManagerBuilder delegateBuilder : delegateBuilders) {
                        delegate = delegateBuilder.getDefaultUserDetailsService();
                        if (delegate != null) {
                            break;
                        }
                    }

                    if (delegate == null) {
                        throw new IllegalStateException("UserDetailsService is required.");
                    }
                    this.delegateBuilders = null;
                }
            }

            return delegate.loadUserByUsername(username);
        }
    }

    static final class AuthenticationManagerDelegator implements AuthenticationManager {
        private AuthenticationManagerBuilder delegateBuilder;
        private AuthenticationManager delegate;
        private final Object delegateMonitor = new Object();
        private Set<String> beanNames;

        AuthenticationManagerDelegator(AuthenticationManagerBuilder delegateBuilder,
                                       ApplicationContext context) {
            Assert.notNull(delegateBuilder, "delegateBuilder cannot be null");
            Field parentAuthMgrField = ReflectionUtils.findField(
                    AuthenticationManagerBuilder.class, "parentAuthenticationManager");
            Assert.notNull(parentAuthMgrField,"cannot reachable");
            ReflectionUtils.makeAccessible(parentAuthMgrField);
            beanNames = getAuthenticationManagerBeanNames(context);
            validateBeanCycle(
                    ReflectionUtils.getField(parentAuthMgrField, delegateBuilder),
                    beanNames);
            this.delegateBuilder = delegateBuilder;
        }

        @Override
        public Authentication authenticate(Authentication authentication)
                throws AuthenticationException {
            if (delegate != null) {
                return delegate.authenticate(authentication);
            }

            synchronized (delegateMonitor) {
                if (delegate == null) {
                    delegate = this.delegateBuilder.getObject();
                    this.delegateBuilder = null;
                }
            }

            return delegate.authenticate(authentication);
        }

        private static Set<String> getAuthenticationManagerBeanNames(
                ApplicationContext applicationContext) {
            String[] beanNamesForType = BeanFactoryUtils
                    .beanNamesForTypeIncludingAncestors(applicationContext,
                            AuthenticationManager.class);
            return new HashSet<>(Arrays.asList(beanNamesForType));
        }

        private static void validateBeanCycle(Object auth, Set<String> beanNames) {
            if (auth != null && !beanNames.isEmpty()) {
                if (auth instanceof Advised) {
                    Advised advised = (Advised) auth;
                    TargetSource targetSource = advised.getTargetSource();
                    if (targetSource instanceof LazyInitTargetSource) {
                        LazyInitTargetSource lits = (LazyInitTargetSource) targetSource;
                        if (beanNames.contains(lits.getTargetBeanName())) {
                            throw new FatalBeanException(
                                    "A dependency cycle was detected when trying to resolve the AuthenticationManager. Please ensure you have configured authentication.");
                        }
                    }
                }
                beanNames = Collections.emptySet();
            }
        }
    }

    static class DefaultPasswordEncoderAuthenticationManagerBuilder extends AuthenticationManagerBuilder {
        private PasswordEncoder defaultPasswordEncoder;


        DefaultPasswordEncoderAuthenticationManagerBuilder(
                ObjectPostProcessor<Object> objectPostProcessor, PasswordEncoder defaultPasswordEncoder) {
            super(objectPostProcessor);
            this.defaultPasswordEncoder = defaultPasswordEncoder;
        }

        @Override
        public InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> inMemoryAuthentication()
                throws Exception {
            return super.inMemoryAuthentication()
                    .passwordEncoder(this.defaultPasswordEncoder);
        }

        @Override
        public JdbcUserDetailsManagerConfigurer<AuthenticationManagerBuilder> jdbcAuthentication()
                throws Exception {
            return super.jdbcAuthentication()
                    .passwordEncoder(this.defaultPasswordEncoder);
        }

        @Override
        public <T extends UserDetailsService> DaoAuthenticationConfigurer<AuthenticationManagerBuilder, T> userDetailsService(
                T userDetailsService) throws Exception {
            return super.userDetailsService(userDetailsService)
                    .passwordEncoder(this.defaultPasswordEncoder);
        }
    }

    static class LazyPasswordEncoder implements PasswordEncoder {
        private ApplicationContext applicationContext;
        private PasswordEncoder passwordEncoder;

        LazyPasswordEncoder(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Override
        public String encode(CharSequence rawPassword) {
            return getPasswordEncoder().encode(rawPassword);
        }

        @Override
        public boolean matches(CharSequence rawPassword,
                               String encodedPassword) {
            return getPasswordEncoder().matches(rawPassword, encodedPassword);
        }

        @Override
        public boolean upgradeEncoding(String encodedPassword) {
            return getPasswordEncoder().upgradeEncoding(encodedPassword);
        }

        private PasswordEncoder getPasswordEncoder() {
            if (this.passwordEncoder != null) {
                return this.passwordEncoder;
            }
            PasswordEncoder passwordEncoder = getBeanOrNull(PasswordEncoder.class);
            if (passwordEncoder == null) {
                passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
            }
            this.passwordEncoder = passwordEncoder;
            return passwordEncoder;
        }

        private <T> T getBeanOrNull(Class<T> type) {
            try {
                return this.applicationContext.getBean(type);
            } catch (NoSuchBeanDefinitionException notFound) {
                return null;
            }
        }

        @Override
        public String toString() {
            return getPasswordEncoder().toString();
        }
    }
}
