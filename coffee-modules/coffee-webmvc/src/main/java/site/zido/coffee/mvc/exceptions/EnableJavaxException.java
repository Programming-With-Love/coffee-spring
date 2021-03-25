package site.zido.coffee.mvc.exceptions;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import site.zido.coffee.mvc.rest.JavaxValidationExceptionAdvice;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({JavaxValidationExceptionAdvice.class})
@Configuration
public @interface EnableJavaxException {
}
