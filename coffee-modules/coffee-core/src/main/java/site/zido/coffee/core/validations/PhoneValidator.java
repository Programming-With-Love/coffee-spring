package site.zido.coffee.core.validations;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static site.zido.coffee.core.constants.Patterns.PHONE_PATTERN;

/**
 * @author zido
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    @Override
    public void initialize(Phone constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || (value.matches(PHONE_PATTERN));
    }
}

