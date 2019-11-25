package site.zido.coffee.security.authentication.phone;

/**
 * 验证码比对
 *
 * @author zido
 */
public interface CodeValidator {

    boolean validate(String originalCode, String inputCode);

}
