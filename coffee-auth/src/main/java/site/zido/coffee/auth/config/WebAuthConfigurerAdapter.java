package site.zido.coffee.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import site.zido.coffee.auth.authentication.AuthenticationManager;
import site.zido.coffee.auth.security.PasswordEncoder;
import site.zido.coffee.auth.user.IUserService;

/**
 * @author zido
 */
public class WebAuthConfigurerAdapter implements WebAuthConfigurer<FilterChainFilterBuilder> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private ApplicationContext context;
    private boolean defaults;
    private WebAuthUnit unit;
    private boolean authenticationManagerInitialized;

    @Override
    public void init(FilterChainFilterBuilder builder) throws Exception {
        final WebAuthUnit unit = getUnit();
        builder.addFilterChainManagerBuilder(unit);
    }

    private WebAuthUnit getUnit() {
        if(unit != null){
            return unit;
        }

    }

    protected AuthenticationManager authenticationManager(){
        if(!authenticationManagerInitialized){
            configure();
        }
    }

    static class DefaultPasswordEncoderAuthenticationManagerBuilder extends AuthenticationManagerBuilder{

        private PasswordEncoder defaultPasswordEncoder;

        public DefaultPasswordEncoderAuthenticationManagerBuilder(
                ObjectPostProcessor<Object> objectPostProcessor,
                PasswordEncoder defaultPasswordEncoder) {
            super(objectPostProcessor);
            this.defaultPasswordEncoder = defaultPasswordEncoder;
        }


    }

    @Override
    public void configure(FilterChainFilterBuilder builder) throws Exception {

    }
}
