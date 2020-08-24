package site.zido.coffee.autoconfigure.security.rest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import site.zido.coffee.security.RestSecurityConfigurationAdapter;

/**
 * 替换{@link WebSecurityConfigurerAdapter},使用{@link RestSecurityConfigurationAdapter}
 *
 * @author zido
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RestSecurityConfigurationAdapter.class)
@ConditionalOnMissingBean(RestSecurityConfigurationAdapter.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
public class SpringBootRestSecurityConfiguration {

    @Configuration(proxyBeanMethods = false)
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    static class DefaultConfigurerAdapter extends RestSecurityConfigurationAdapter {

    }

}
