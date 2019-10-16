package site.zido.coffee.auth.user;

import org.springframework.core.annotation.AnnotatedElementUtils;
import site.zido.coffee.auth.user.annotations.AuthColumnPassword;

import java.lang.reflect.Field;

/**
 * @author zido
 */
public class PasswordUserReader extends AbstractAnnotatedUserDetailsReader<PasswordUser, PasswordUserBuilder> {
    public PasswordUserReader(Class<?> userClass) {
        super(userClass);
    }

    @Override
    protected void additionalParse(PasswordUserBuilder udb, Class<?> userClass, Field field) {
        AuthColumnPassword authColumnPasswordAnnotation =
                AnnotatedElementUtils.findMergedAnnotation(field, AuthColumnPassword.class);
        if (authColumnPasswordAnnotation != null) {
            udb.setPasswordField(field);
        }
    }

    @Override
    protected PasswordUserBuilder createBuilder(Class<?> userClass) {
        return new PasswordUserBuilder();
    }

    public Field getPasswordField() {
        return super.builder.getPasswordField();
    }
}
