package site.zido.coffee.auth.entity.annotations;

import java.lang.annotation.*;

/**
 * 标记字段为微信unionId,将用于登录
 *
 * @author zido
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthColumnWechatUnionId {
}
