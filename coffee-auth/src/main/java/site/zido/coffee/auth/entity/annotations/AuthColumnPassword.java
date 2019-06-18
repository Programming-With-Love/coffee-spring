package site.zido.coffee.auth.entity.annotations;

import java.lang.annotation.*;

/**
 * 标记字段为用户密码(存储时自动加密,认证完成后自动去除该字段)
 *
 * @author zido
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthColumnPassword {
    /**
     * 是否加密
     *
     * @return true/false
     */
    boolean encode() default false;
}
