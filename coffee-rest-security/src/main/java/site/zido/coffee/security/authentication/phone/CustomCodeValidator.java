package site.zido.coffee.security.authentication.phone;

import java.util.Objects;

/**
 * @author zido
 */
public class CustomCodeValidator implements CodeValidator {
    private boolean ignoreCase;

    public CustomCodeValidator() {
        this(true);
    }

    public CustomCodeValidator(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    @Override
    public boolean validate(String originalCode, String inputCode) {
        return ignoreCase ? (originalCode != null && originalCode.equalsIgnoreCase(inputCode)) : Objects.equals(originalCode, inputCode);
    }
}
