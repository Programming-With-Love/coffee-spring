package site.zido.coffee.auth.user.annotations;

import java.lang.annotation.*;

/**
 * 标记账户是否可用字段
 *
 * @author zido
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthColumnEnabled {

}
