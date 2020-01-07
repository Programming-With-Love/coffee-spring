package site.zido.coffee.security.authentication.phone;

/**
 * 手机号验证码发送接口
 *
 * @author zido
 */
public interface PhoneCodeService {
    /**
     * 发送验证码
     *
     * @param mobile 手机号
     * @param code   验证码
     */
    void sendCode(String mobile, String code);
}
