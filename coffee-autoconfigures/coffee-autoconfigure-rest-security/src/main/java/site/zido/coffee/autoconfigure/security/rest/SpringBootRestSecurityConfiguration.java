package site.zido.coffee.autoconfigure.security.rest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.util.StringUtils;
import site.zido.coffee.core.utils.RandomUtils;
import site.zido.coffee.security.authentication.RestAuthenticationFailureHandler;
import site.zido.coffee.security.authentication.RestAuthenticationSuccessHandler;
import site.zido.coffee.security.configurers.PhoneCodeLoginConfigurer;
import site.zido.coffee.security.configurers.RestSecurityConfigureAdapter;
import site.zido.coffee.security.configurers.RestSecurityContextConfigurer;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({RestSecurityConfigureAdapter.class, WebSecurityConfigurerAdapter.class})
@ConditionalOnMissingBean({RestSecurityConfigureAdapter.class, WebSecurityConfigurerAdapter.class})
@ConditionalOnWebApplication(type = Type.SERVLET)
public class SpringBootRestSecurityConfiguration {

    @Configuration(proxyBeanMethods = false)
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    @EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
    @EnableConfigurationProperties(CoffeeSecurityProperties.class)
    public static class DefaultRestSecurityConfigurerAdapter extends RestSecurityConfigureAdapter {
        private final CoffeeSecurityProperties properties;

        public DefaultRestSecurityConfigurerAdapter(CoffeeSecurityProperties properties) {
            super(false);
            this.properties = properties;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);
            http
                    //允许所有的请求，因为restful api使用注解来进行权限处理
                    .authorizeRequests().anyRequest().permitAll()
                    .and()
                    //帐号密码登录
                    .formLogin();
            if (properties.getPhoneCodeEnable()) {
                PhoneCodeLoginConfigurer<HttpSecurity> phoneCodeConfigure
                        = http.apply(new PhoneCodeLoginConfigurer<>())
                        .successHandler(new RestAuthenticationSuccessHandler())
                        .failureHandler(new RestAuthenticationFailureHandler());
                if (StringUtils.hasText(properties.getPhoneCode().getKeyPrefix())) {
                    phoneCodeConfigure.codeCachePrefix(properties.getPhoneCode().getKeyPrefix());
                }
                if (StringUtils.hasText(properties.getPhoneCode().getProcessUrl())) {
                    phoneCodeConfigure.loginProcessingUrl(properties.getPhoneCode().getProcessUrl());
                } else {
                    phoneCodeConfigure.loginProcessingUrl("/users/sms/sessions");
                }
                if (StringUtils.hasText(properties.getPhoneCode().getCodeProcessUrl())) {
                    phoneCodeConfigure.codeProcessingUrl(properties.getPhoneCode().getCodeProcessUrl());
                }
            }
            switch (properties.getSecureStoreType()) {
                case COOKIE:
                    http.securityContext().and()
                            .sessionManagement();
                    break;
                case TOKEN:
                    //暂不支持
                    throw new UnsupportedOperationException("暂不支持token，请选择jwt。注意：此token指后端存储的token，这与session类似");
                case JWT:
                default:
                    //自定义jwt的超时时间
                    RestSecurityContextConfigurer<HttpSecurity>.JwtSecurityConfigurer jwt
                            = http.apply(new RestSecurityContextConfigurer<>()).jwt();
                    String secret = properties.getJwt().getSecret();
                    if (properties.getJwt().getAutoRefresh()) {
                        jwt.autoRefresh(true);
                        jwt.secret(secret);
                        jwt.refreshSecret(secret);
                    } else {
                        jwt.autoRefresh(false);
                    }
                    if (properties.getJwt().getRefreshSupport()) {
                        jwt.refresh(true);
                        if (StringUtils.hasLength(properties.getJwt().getRefreshSecret())) {
                            jwt.refreshSecret(properties.getJwt().getRefreshSecret());
                        }
                    } else {
                        jwt.refresh(false);
                    }
            }
        }
    }

}
