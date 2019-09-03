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
import site.zido.coffee.auth.authentication.logout.LogoutSuccessHandler;
import site.zido.coffee.auth.authentication.logout.RestLogoutSuccessHandler;
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

    @Bean
    @ConditionalOnMissingBean(SecuritySessionStrategy.class)
    public SecuritySessionStrategy strategy() {
        return new DefaultSecuritySessionStrategy();
    }

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

    @Bean
    @ConditionalOnMissingBean(LogoutSuccessHandler.class)
    public LogoutSuccessHandler restLogoutSuccessHandler() {
        return new RestLogoutSuccessHandler(responseBodyFactory, mapper);
    }

    @Autowired(required = false)
    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

}
