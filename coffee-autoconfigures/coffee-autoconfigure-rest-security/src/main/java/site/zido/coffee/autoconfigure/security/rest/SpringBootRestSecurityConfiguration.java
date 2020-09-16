package site.zido.coffee.autoconfigure.security.rest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import site.zido.coffee.security.configurers.DefaultRestSecurityConfigureAdapter;
import site.zido.coffee.security.configurers.RestSecurityContextConfigurer;

@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(WebSecurityConfigurerAdapter.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
public class SpringBootRestSecurityConfiguration {

    @Configuration(proxyBeanMethods = false)
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    @EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
    @EnableConfigurationProperties(CoffeeSecurityProperties.class)
    @ConditionalOnProperty(value = "secureStoreType", prefix = "spring.security", havingValue = "JWT", matchIfMissing = true)
    static class DefaultSecurityConfigurerAdapter extends DefaultRestSecurityConfigureAdapter {
        private final CoffeeSecurityProperties properties;

        public DefaultSecurityConfigurerAdapter(CoffeeSecurityProperties properties) {
            super(false);
            this.properties = properties;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    //权限管理将管理所有的请求
                    .authorizeRequests().anyRequest().permitAll()
                    .and()
                    //帐号密码登录
                    .formLogin();
            //TODO 手机号验证码登录
            //自定义jwt的超时时间
            RestSecurityContextConfigurer<HttpSecurity>.JwtSecurityConfigurer jwt
                    = http.apply(new RestSecurityContextConfigurer<>()).jwt();
            if (properties.getJwt().getAutoRefresh()) {
                jwt.autoRefresh(true);
                String secret = properties.getJwt().getSecret();
                if (secret == null) {
                    secret = RandomUtils.ascii(12);
                }
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
