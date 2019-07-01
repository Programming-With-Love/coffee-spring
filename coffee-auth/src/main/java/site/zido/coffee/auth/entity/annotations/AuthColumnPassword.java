package site.zido.coffee.auth.entity.annotations;

import site.zido.coffee.auth.handlers.NoPasswordEncoder;
import site.zido.coffee.auth.handlers.PasswordEncoder;

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
    Class<? extends PasswordEncoder> encodeClass() default NoPasswordEncoder.class;
}
