package site.zido.coffee.mvc.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(MvcLogSelector.class)
@Configuration
public @interface EnableRequestLogger {
    boolean enable() default false;

}
