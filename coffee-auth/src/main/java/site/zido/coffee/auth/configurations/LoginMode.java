package site.zido.coffee.auth.configurations;

public enum LoginMode {
    /**
     * 手机号验证码
     */
    MOBILE_CODE,
    /**
     * 帐号密码
     */
    USERNAME_PASSWORD,
    /**
     * 自动登录(需要在适用的环境，比如在公众号中微信自动登录)
     */
    AUTO_LOGIN,
    /**
     * 启用所有
     */
    ALL,
}
