package site.zido.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zido
 */
@EnableRestSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthConfig extends RestSecurityConfigurationAdapter {

    @Bean
    public GlobalResultHandler handler(HttpResponseBodyFactory factory) {
        return new GlobalResultHandler(factory);
    }

    @Override
    protected void configure(RestHttpSecurity http) throws Exception {
        http
                .authorizeRequests().anyRequest().permitAll()
                .and()
                .formLogin().and()
                .phoneCodeLogin().phoneCodeCache(new PhoneCodeCache() {

            @Override
            public void put(String phone, String code) {
            }

            @Override
            public String getCode(String phone) {
                return "1234";
            }
        }).and()
                .securityContext().jwt().jwtExpiration(1, TimeUnit.HOURS).and()
                .httpBasic();
    }

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
