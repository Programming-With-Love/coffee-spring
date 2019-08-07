package site.zido.coffee.auth.user.annotations;

import java.lang.annotation.*;

/**
 * 标记字段为用户名
 *
 * @author zido
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthColumnUsername {
}
