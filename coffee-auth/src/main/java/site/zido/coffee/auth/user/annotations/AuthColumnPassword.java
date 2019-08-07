package site.zido.coffee.auth.user.annotations;

import site.zido.coffee.auth.security.NoPasswordEncoder;
import site.zido.coffee.auth.security.PasswordEncoder;

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
