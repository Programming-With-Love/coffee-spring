package site.zido.coffee.common.validations.validations;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 手机号校验注解
 *
 * @author zido
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MobileValidator.class)
public @interface Mobile {
    String message() default "手机号错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
