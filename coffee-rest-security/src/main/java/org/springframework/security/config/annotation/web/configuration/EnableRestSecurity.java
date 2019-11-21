package org.springframework.security.config.annotation.web.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author zido
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
@Import({RestSecurityConfiguration.class,
        SpringWebMvcImportSelector.class,
        OAuth2ImportSelector.class})
@EnableGlobalAuthentication
@Configuration
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public @interface EnableRestSecurity {
    /**
     * Controls debugging support for Spring Security. Default is false.
     *
     * @return if true, enables debug support with Spring Security
     */
    boolean debug() default false;
}
