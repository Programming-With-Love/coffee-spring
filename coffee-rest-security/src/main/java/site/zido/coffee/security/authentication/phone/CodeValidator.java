package site.zido.coffee.security.authentication.phone;

/**
 * 验证码比对
 *
 * @author zido
 */
public interface CodeValidator {

    /**
     * 验证code
     * @param originalCode 原code
     * @param inputCode 输入的code
     * @return true/false
     */
    boolean validate(String originalCode, String inputCode);

}
