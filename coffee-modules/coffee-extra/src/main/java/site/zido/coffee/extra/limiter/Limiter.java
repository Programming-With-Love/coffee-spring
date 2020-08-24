package site.zido.coffee.extra.limiter;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 限流器注解，可以根据任何资源进行限流{@link #key()}
 *
 * @author zido
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Limiter {
    String key() default "";

    long timeout() default 55;

    TimeUnit unit() default TimeUnit.SECONDS;
}
