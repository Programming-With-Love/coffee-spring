package site.zido.coffee.auth.authentication;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 认证注解，放在controller类或者方法上,拦截器会进行相关拦截,默认为匿名用户(不需要登陆)
 *
 * @author zido
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {

    @AliasFor("role")
    String[] value() default "";

    @AliasFor("value")
    String[] role() default "";
}
