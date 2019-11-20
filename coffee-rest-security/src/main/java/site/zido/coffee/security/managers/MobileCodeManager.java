package site.zido.coffee.security.managers;

/**
 * 手机号验证码发送接口
 *
 * @author zido
 */
public interface MobileCodeManager {
    /**
     * 发送验证码
     *
     * @param mobile 手机号
     */
    void sendCode(String mobile);

    /**
     * 验证手机号验证码
     *
     * @param mobile 手机号
     * @param code   验证码
     * @return true/false
     */
    boolean validateCode(String mobile, String code);
}
