package site.zido.coffee.autoconfigure.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import site.zido.coffee.mvc.logger.EnableRequestLogger;

@EnableRequestLogger
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(value = "spring.coffee.web.request-log", havingValue = "true")
public class RequestLogEnablerConfiguration {
}
