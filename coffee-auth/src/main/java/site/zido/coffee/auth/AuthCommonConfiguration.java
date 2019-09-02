package site.zido.coffee.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import site.zido.coffee.CommonAutoConfiguration;
import site.zido.coffee.auth.config.*;
import site.zido.coffee.auth.context.AuthPrincipalArgumentResolver;
import site.zido.coffee.auth.context.HttpSessionUserContextRepository;
import site.zido.coffee.auth.context.UserContextRepository;
import site.zido.coffee.auth.handlers.*;
import site.zido.coffee.auth.web.session.DefaultSecuritySessionStrategy;
import site.zido.coffee.auth.web.session.SecuritySessionStrategy;
import site.zido.coffee.common.rest.HttpResponseBodyFactory;

import java.util.List;

/**
 * @author zido
 */
@Configuration
@AutoConfigureAfter({
        CommonAutoConfiguration.JsonAutoConfiguration.class
})
public class AuthCommonConfiguration {

    private HttpResponseBodyFactory responseBodyFactory;
    private ObjectMapper mapper;

    @Bean
    @ConditionalOnMissingBean(DisabledUserHandler.class)
    public DisabledUserHandler disabledUserHandler() {
        return new RestDisabledUserHandler(responseBodyFactory, mapper);
    }

    @Bean
    @ConditionalOnMissingBean(LoginSuccessHandler.class)
    public LoginSuccessHandler loginSuccessHandler() {
        return new RestLoginSuccessHandler(responseBodyFactory, mapper);
    }

    @Bean
    @ConditionalOnMissingBean(LoginFailureHandler.class)
    public LoginFailureHandler loginFailureHandler() {
        return new RestLoginFailureHandler(responseBodyFactory, mapper);
    }

    @Bean
    @ConditionalOnMissingBean(LoginExpectedHandler.class)
    public LoginExpectedHandler loginExpectedHandler() {
        return new RestLoginExceptedHandler(responseBodyFactory, mapper);
    }

    @Bean
    @ConditionalOnMissingBean(UsernamePasswordAuthenticationFilterFactory.class)
    public UsernamePasswordAuthenticationFilterFactory usernamePasswordAuthenticationFilterFactory() {
        return new UsernamePasswordAuthenticationFilterFactory();
    }

    //    @Bean
    //    @ConditionalOnMissingBean(WechatAuthenticator.class)
    //    @ConditionalOnProperty({"auth.wechat.global.appId", "auth.wechat.global.appSecret"})
    //    public WechatAuthenticator wechatAuthenticator(@Value("${auth.wechat.global.appId}") String appId,
    //                                                   @Value("${auth.wechat.global.appSecret}") String appSecret) {
    //        WechatAuthenticator wechatAuthenticator = new WechatAuthenticator();
    //        wechatAuthenticator.setAppId(appId);
    //        wechatAuthenticator.setAppSecret(appSecret);
    //        return wechatAuthenticator;
    //    }

    @Bean
    @ConditionalOnMissingBean(SecuritySessionStrategy.class)
    public SecuritySessionStrategy strategy() {
        return new DefaultSecuritySessionStrategy();
    }

//        @Bean
//        @ConditionalOnMissingBean(UserManager.class)
//        public UserManager userManager(EntityManager manager) {
//            return new JpaSessionUserManager(manager);
//        }

    @Autowired(required = false)
    public void setResponseBodyFactory(HttpResponseBodyFactory responseBodyFactory) {
        this.responseBodyFactory = responseBodyFactory;
    }

    @Bean
    @ConditionalOnMissingBean(DefaultAuthBeforeUserFiltersFactory.class)
    public UserFiltersFactory beforeUserFiltersFactory() {
        return new DefaultAuthBeforeUserFiltersFactory();
    }

    @Bean
    @ConditionalOnMissingBean(UserContextRepository.class)
    public HttpSessionUserContextRepository userContextRepository() {
        return new HttpSessionUserContextRepository();
    }

    @Configuration
    @Import(ObjectPostProcessorConfiguration.class)
    @AutoConfigureAfter(
            JpaRepositoriesAutoConfiguration.class)
    static class BuilderConfiguration {
        @Bean
        public FilterChainFilterFactoryBean coffeeAuthBuilders(ObjectPostProcessor<Object> opp) {
            return new FilterChainFilterFactoryBean(opp);
        }
    }

    @Configuration
    static class WebMvcAuthConfiguration extends WebMvcConfigurerAdapter {
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
            argumentResolvers.add(new AuthPrincipalArgumentResolver());
        }
    }

    @Autowired(required = false)
    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

//        @Configuration
//        class PermissionInterceptorConfiguration extends WebMvcConfigurerAdapter {
//            private EntityManager manager;
//
//            @Override
//            public void addInterceptors(InterceptorRegistry registry) {
//                PermissionInterceptor interceptor = new PermissionInterceptor();
//                interceptor.setDisabledUserHandler(disabledUserHandler());
//                interceptor.setLoginExpectedHandler(loginExpectedHandler());
//                interceptor.setUserManager(userManager(manager));
//                registry.addInterceptor(interceptor);
//            }
//
//            @Autowired
//            public void setManager(EntityManager manager) {
//                this.manager = manager;
//            }
//        }


}
