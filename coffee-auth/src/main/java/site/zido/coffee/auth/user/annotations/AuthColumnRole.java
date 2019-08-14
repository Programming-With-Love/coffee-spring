package site.zido.coffee.auth.user.annotations;

import java.lang.annotation.*;

/**
 * 标记字段值为用户角色
 *
 * @author zido
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthColumnRole {
}
