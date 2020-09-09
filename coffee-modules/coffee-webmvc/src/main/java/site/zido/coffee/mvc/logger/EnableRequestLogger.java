package site.zido.coffee.mvc.logger;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(MvcLogConfiguration.class)
@Configuration
public @interface EnableRequestLogger {
}
