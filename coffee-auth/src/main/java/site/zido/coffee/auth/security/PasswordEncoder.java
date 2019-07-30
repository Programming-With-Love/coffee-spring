package site.zido.coffee.auth.security;

/**
 * @author zido
 */
public interface PasswordEncoder {
    /**
     * 加密
     *
     * @param password password
     * @return 加密后的字符串
     */
    String encode(String password);

    /**
     * 验证
     *
     * @param originPassword  原密码
     * @param encodedPassword 加密后的密码
     * @return true/false
     */
    boolean validate(String originPassword, String encodedPassword);
}
