package site.zido.coffee.auth.entity.annotations;

import java.lang.annotation.*;

/**
 * 标记字段为session存储字段
 *
 * @author zido
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthColumnKey {
}
