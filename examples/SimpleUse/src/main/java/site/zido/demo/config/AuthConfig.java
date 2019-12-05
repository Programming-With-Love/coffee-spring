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
import site.zido.coffee.security.RestSecurityConfigurationAdapter;
import site.zido.coffee.security.authentication.phone.PhoneCodeCache;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zido
 */
@EnableRestSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthConfig extends RestSecurityConfigurationAdapter {

    @Override
    protected void configure(RestHttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin().and()
                .phoneCodeLogin().phoneCodeCache(new PhoneCodeCache() {
            private Map<String, String> map = new HashMap<>();

            @Override
            public void put(String phone, String code) {
                map.put(phone, code);
            }

            @Override
            public String getCode(String phone) {
                return map.get(phone);
            }
        }).and()
                .securityContext().jwt().and()
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
        return new InMemoryUserDetailsManager(user, user2);
    }

}
