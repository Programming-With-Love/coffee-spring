package site.zido.coffee.security.validations;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author zido
 */
public class MobileValidator implements ConstraintValidator<Mobile, String> {
    private static final String MOBILE_PATTERN = "^1[123456789][\\d]{9}";

    @Override
    public void initialize(Mobile constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || (value.matches(MOBILE_PATTERN));
    }
}

