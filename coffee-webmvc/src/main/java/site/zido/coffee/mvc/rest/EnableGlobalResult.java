package site.zido.coffee.mvc.rest;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 是否允许全局使用统一返回值
 * <p>
 * 如果全局使用统一返回值，则成功和失败结果均使用{@link Result}进行统一封装，
 * 否则仅当失败时使用统一的Result返回失败异常
 *
 * @author zido
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(GlobalRestConfiguration.class)
@Configuration
public @interface EnableGlobalResult {

}
