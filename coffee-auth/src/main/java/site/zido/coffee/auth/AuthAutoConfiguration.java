package site.zido.coffee.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.util.UrlPathHelper;
import site.zido.coffee.CommonAutoConfiguration;
import site.zido.coffee.auth.handlers.PermissionInterceptor;
import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.entity.annotations.AuthEntity;
import site.zido.coffee.auth.handlers.*;
import site.zido.coffee.auth.authentication.*;
import site.zido.coffee.common.rest.DefaultHttpResponseBodyFactory;
import site.zido.coffee.common.rest.HttpResponseBodyFactory;

import java.util.*;

import static site.zido.coffee.auth.Constants.DEFAULT_LOGIN_URL;

/**
 * @author zido
 */
@Configuration
@AutoConfigureAfter({
        JpaRepositoriesAutoConfiguration.class,
        CommonAutoConfiguration.JsonAutoConfiguration.class
})
public class AuthAutoConfiguration {

    private UrlPathHelper urlPathHelper;
    private HttpResponseBodyFactory responseBodyFactory;
    private ObjectMapper mapper;

    /**
     * 注册过滤器,用于认证/授权
     *
     * @return filter
     */
    @Bean
    @ConditionalOnMissingBean(AuthenticationFilter.class)
    public AuthenticationFilter getFilter() {
        AuthenticationFilter filter = new AuthenticationFilter();
        filter.setUrlPathHelper(urlPathHelper);
        filter.setAuthenticationFailureHandler(loginFailureHandler());
        filter.setAuthenticationSuccessHandler(loginSuccessHandler());
        filter.setUserManager(userManager());
        return filter;
    }

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
    @ConditionalOnMissingBean(UsernamePasswordAuthenticator.class)
    public UsernamePasswordAuthenticator usernamePasswordAuthenticator() {
        return new UsernamePasswordAuthenticator();
    }

    @Bean
    @ConditionalOnMissingBean(WechatAuthenticator.class)
    @ConditionalOnProperty({"auth.wechat.global.appId", "auth.wechat.global.appSecret"})
    public WechatAuthenticator wechatAuthenticator(@Value("${auth.wechat.global.appId}") String appId,
                                                   @Value("${auth.wechat.global.appSecret}") String appSecret) {
        WechatAuthenticator wechatAuthenticator = new WechatAuthenticator();
        wechatAuthenticator.setAppId(appId);
        wechatAuthenticator.setAppSecret(appSecret);
        return wechatAuthenticator;
    }

    @Bean
    @ConditionalOnMissingBean(UserManager.class)
    public UserManager userManager() {
        return new JpaSessionUserManager();
    }

    @Autowired(required = false)
    public void setPathUrlHelper(UrlPathHelper helper) {
        this.urlPathHelper = helper;
    }


    @Autowired
    public void setResponseBodyFactory(HttpResponseBodyFactory responseBodyFactory) {
        this.responseBodyFactory = responseBodyFactory;
    }

    @Autowired
    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Configuration
    class PermissionInterceptorConfiguration extends WebMvcConfigurerAdapter {
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            PermissionInterceptor interceptor = new PermissionInterceptor();
            interceptor.setDisabledUserHandler(disabledUserHandler());
            interceptor.setLoginExpectedHandler(loginExpectedHandler());
            interceptor.setUserManager(userManager());
            registry.addInterceptor(interceptor);
        }
    }
}
