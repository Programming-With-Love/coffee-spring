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
import site.zido.coffee.security.RestSecurityConfigurationAdapter;

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
                .phoneCodeLogin().and()
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
        return new InMemoryUserDetailsManager(user);
    }

}
