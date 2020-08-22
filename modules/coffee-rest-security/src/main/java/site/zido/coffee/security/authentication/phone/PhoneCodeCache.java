package site.zido.coffee.security.authentication.phone;

/**
 * 缓存手机号验证码接口
 *
 * @author zido
 */
public interface PhoneCodeCache {
    /**
     * 放入手机号所属验证码
     *
     * @param phone 手机号
     * @param code  验证码
     */
    void put(String phone, String code);

    /**
     * 获取手机号验证码
     *
     * @param phone 手机号
     * @return 验证码
     */
    String getCode(String phone);
}
