package org.springframework.security.config.annotation.web.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 开启rest风格的Security配置
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
@Import({RestSecurityConfiguration.class,
        SpringWebMvcImportSelector.class,
        OAuth2ImportSelector.class})
@EnableGlobalAuthentication
@Configuration
public @interface EnableRestSecurity {
    /**
     * Controls debugging support for Spring Security. Default is false.
     *
     * @return if true, enables debug support with Spring Security
     */
    boolean debug() default false;
}
