package site.zido.coffee.autoconfigure.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;

/**
 * @author zido
 */
@Configuration
@ConditionalOnClass(name = "javax.validation.ConstraintViolationException")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class JavaxExceptionEnablerConfiguration {
}
