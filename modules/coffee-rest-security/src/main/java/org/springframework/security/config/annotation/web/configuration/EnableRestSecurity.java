package org.springframework.security.config.annotation.web.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import site.zido.coffee.security.configurers.RestSecurityAutoConfiguration;
import site.zido.coffee.security.configurers.RestSecurityFilterAutoConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 使用@EnableRestSecurity注解替换原spring.factor的自动配置
 * <p>
 * 排除默认针对web的自动配置，{@link SecurityAutoConfiguration}
 * 和{@link SecurityFilterAutoConfiguration}会对rest自动配置有所影响
 * <p>
 * 保留{@link UserDetailsServiceAutoConfiguration}
 * <p>
 * 新引入{@link RestSecurityAutoConfiguration}和{@link RestSecurityFilterAutoConfiguration}，配置内容大致相同，
 * 不过调整了配置之间的互相依赖
 *
 * @author zido
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
@Import({RestSecurityAutoConfiguration.class,
        RestSecurityFilterAutoConfiguration.class,
        RestSecurityConfiguration.class,
        SpringWebMvcImportSelector.class,
        OAuth2ImportSelector.class})
@EnableGlobalAuthentication
@Configuration
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class})
public @interface EnableRestSecurity {
    /**
     * Controls debugging support for Spring Security. Default is false.
     *
     * @return if true, enables debug support with Spring Security
     */
    boolean debug() default false;
}
