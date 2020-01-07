package site.zido.coffee.security.authentication.phone;

/**
 * 验证码生成器
 *
 * @author zido
 */
public interface CodeGenerator {
    /**
     * 生成验证码
     *
     * @param phone 手机号
     * @return code
     */
    String generateCode(String phone);
}
