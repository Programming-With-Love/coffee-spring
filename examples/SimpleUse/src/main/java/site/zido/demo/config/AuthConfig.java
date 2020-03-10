package site.zido.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.RestHttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableRestSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import site.zido.coffee.common.rest.GlobalResultHandler;
import site.zido.coffee.common.rest.HttpResponseBodyFactory;
import site.zido.coffee.security.RestSecurityConfigurationAdapter;
import site.zido.coffee.security.authentication.phone.PhoneCodeCache;
import site.zido.coffee.security.authentication.phone.SpringRedisPhoneCodeCache;

import java.util.concurrent.TimeUnit;

/**
 * 认证配置类，restful风格，使用jwt方案
 *
 * @author zido
 */
@EnableRestSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthConfig extends RestSecurityConfigurationAdapter {
    @Override
    protected void configure(RestHttpSecurity http) throws Exception {
        http
                //权限管理将管理所有的请求
                .authorizeRequests().anyRequest().permitAll()
                .and()
                //帐号密码登录
                .formLogin().and()
                //手机号验证码登录
                .phoneCodeLogin().and()
                //自定义jwt的超时时间
                .securityContext().jwt().jwtExpiration(1, TimeUnit.HOURS);
    }

    /**
     * 创建几个内存用户，正常使用时，需要自定义userDetailsService
     *
     * @return userDetailsService
     */
    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        UserDetails user = User.builder().username("user")
                .password("user")
                .passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder()::encode)
                .roles("user")
                .build();
        UserDetails user2 = User.builder().username("13512341234")
                .password("xxx")
                .roles("user")
                .passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder()::encode)
                .build();
        UserDetails user3 = User.builder().username("13512341235")
                .password("xxx")
                .roles("admin")
                .passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder()::encode)
                .build();
        return new InMemoryUserDetailsManager(user, user2, user3);
    }

}