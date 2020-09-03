package site.zido.coffee.autoconfigure.security.rest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.configuration.EnableRestSecurity;
import site.zido.coffee.security.RestSecurityConfigurationAdapter;

@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(RestSecurityConfigurationAdapter.class)
@ConditionalOnMissingBean(name = BeanIds.SPRING_SECURITY_FILTER_CHAIN)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableRestSecurity
public class RestSecurityEnableConfiguration {
}
