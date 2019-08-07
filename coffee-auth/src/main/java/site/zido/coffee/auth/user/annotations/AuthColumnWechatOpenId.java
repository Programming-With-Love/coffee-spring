package site.zido.coffee.auth.user.annotations;

import java.lang.annotation.*;

/**
 * 标记字段为微信openId,将用于微信登录
 *
 * @author zido
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthColumnWechatOpenId {
}
