package site.zido.coffee.auth.entity.annotations;

import java.lang.annotation.*;

/**
 * 标记该表为用户表(如果只有一个表可以不使用该注解,当有多个用户表时，需要使用该注解标记对应对应url),将用于登录
 *
 * @author zido
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthEntity {
    String url();
}
