package site.zido.coffee.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import site.zido.coffee.auth.authentication.AuthenticationManager;
import site.zido.coffee.auth.security.NullPasswordEncoder;
import site.zido.coffee.auth.security.PasswordEncoder;

/**
 * @author zido
 */
public abstract class WebAuthConfigurerAdapter implements WebAuthConfigurer<FilterChainFilterBuilder>, ApplicationContextAware {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private WebAuthUnit unit;
    private ObjectPostProcessor<Object> objectPostProcessor;
    private boolean authenticationManagerInitialized;
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    private AuthenticationManager authenticationManager;

    protected WebAuthConfigurerAdapter() {
    }

    @Override
    public void init(FilterChainFilterBuilder builder) throws Exception {
        final WebAuthUnit unit = getUnit();
        builder.addFilterChainManagerBuilder(unit);
    }

    private WebAuthUnit getUnit() throws Exception {
        if (unit != null) {
            return unit;
        }
        AuthenticationManager authenticationManager = authenticationManager();
        unit = new WebAuthUnit(objectPostProcessor);
        configure(unit);
        return unit;
    }

    protected void configure(WebAuthUnit unit) {

    }

    protected AuthenticationManager authenticationManager() throws Exception {
        if (!authenticationManagerInitialized) {
            authenticationManager = authenticationManagerBuilder.build();
            authenticationManagerInitialized = true;
        }
        return authenticationManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        ObjectPostProcessor<Object> objectPostProcessor = context.getBean(ObjectPostProcessor.class);
        LazyPasswordEncoder passwordEncoder = new LazyPasswordEncoder(context);
        authenticationManagerBuilder = new DefaultPasswordEncoderAuthenticationManagerBuilder(objectPostProcessor, passwordEncoder);
    }

    @Autowired
    public void setObjectPostProcessor(ObjectPostProcessor<Object> objectPostProcessor) {
        this.objectPostProcessor = objectPostProcessor;
    }

    static class DefaultPasswordEncoderAuthenticationManagerBuilder extends AuthenticationManagerBuilder {

        private PasswordEncoder defaultPasswordEncoder;

        public DefaultPasswordEncoderAuthenticationManagerBuilder(
                ObjectPostProcessor<Object> objectPostProcessor,
                PasswordEncoder defaultPasswordEncoder) {
            super(objectPostProcessor);
            this.defaultPasswordEncoder = defaultPasswordEncoder;
        }
    }

    static class LazyPasswordEncoder implements PasswordEncoder {
        private ApplicationContext applicationContext;
        private PasswordEncoder passwordEncoder;

        LazyPasswordEncoder(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        private PasswordEncoder getPasswordEncoder() {
            if (this.passwordEncoder != null) {
                return this.passwordEncoder;
            }
            PasswordEncoder passwordEncoder = getBeanOrNull(PasswordEncoder.class);
            if (passwordEncoder == null) {
                //默认不使用密码加密
                passwordEncoder = new NullPasswordEncoder();
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

        @Override
        public String encode(String password) {
            return getPasswordEncoder().encode(password);
        }

        @Override
        public boolean validate(String originPassword, String encodedPassword) {
            return getPasswordEncoder().validate(originPassword, encodedPassword);
        }
    }

    @Override
    public void configure(FilterChainFilterBuilder builder) throws Exception {

    }
}
