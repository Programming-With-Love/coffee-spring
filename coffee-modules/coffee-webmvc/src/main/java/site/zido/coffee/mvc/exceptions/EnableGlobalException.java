package site.zido.coffee.mvc.exceptions;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


/**
 * 是否允许全局使用统一异常处理，会将异常使用{@link site.zido.coffee.mvc.rest.HttpResponseBodyFactory}进行处理
 * <p>
 *
 * @author zido
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(GlobalExceptionConfiguration.class)
@Configuration
public @interface EnableGlobalException {
}
