package site.zido.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * 认证配置类，restful风格，使用jwt方案
 *
 * @author zido
 */
@Configuration
@EnableWebSecurity(debug = true)
public class AuthConfig {
    /**
     * 创建几个内存用户，正常使用时，需要自定义userDetailsService
     *
     * @return userDetailsService
     */
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