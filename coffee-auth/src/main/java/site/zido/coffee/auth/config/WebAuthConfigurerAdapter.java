package site.zido.coffee.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import site.zido.coffee.auth.authentication.AuthenticationManager;
import site.zido.coffee.auth.security.NullPasswordEncoder;
import site.zido.coffee.auth.security.PasswordEncoder;
import site.zido.coffee.auth.user.IUserService;

/**
 * @author zido
 */
public class WebAuthConfigurerAdapter implements WebAuthConfigurer<FilterChainFilterBuilder>, ApplicationContextAware {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private ApplicationContext context;
    private boolean defaults;
    private WebAuthUnit unit;
    private boolean authenticationManagerInitialized;
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Override
    public void init(FilterChainFilterBuilder builder) throws Exception {
        final WebAuthUnit unit = getUnit();
        builder.addFilterChainManagerBuilder(unit);
    }

    private WebAuthUnit getUnit() {
        if (unit != null) {
            return unit;
        }
        AuthenticationManager authenticationManager = authenticationManager();
        return unit;
    }

    protected AuthenticationManager authenticationManager() {
        if (!authenticationManagerInitialized) {
            configure();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = context;
        ObjectPostProcessor objectPostProcessor = context.getBean(ObjectPostProcessor.class);
        LazyPasswordEncoder passwordEncoder = new LazyPasswordEncoder(context);
        authenticationManagerBuilder = new DefaultPasswordEncoderAuthenticationManagerBuilder(objectPostProcessor, passwordEncoder) {
            @Override
            public AuthenticationManagerBuilder eraseCredentials(boolean eraseCredentials) {
                authenticationManagerBuilder.eraseCredentials(eraseCredentials);
                return super.eraseCredentials(eraseCredentials);
            }
        };

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
