package site.zido.coffee.autoconfigure.web;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;

import site.zido.coffee.mvc.exceptions.EnableGlobalException;

@EnableGlobalException
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(value = "spring.coffee.web.global-exception", matchIfMissing = true, havingValue = "true")
@AutoConfigureAfter(GlobalResultEnablerConfiguration.class)
public class GlobalExceptionEnablerConfiguration {

}
