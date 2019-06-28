package site.zido.coffee.auth.configurations;

public class AuthProperties {

    private String wechatAppId;

    private String wechatAppSecret;

    public String getWechatAppId() {
        return wechatAppId;
    }

    public void setWechatAppId(String wechatAppId) {
        this.wechatAppId = wechatAppId;
    }

    public String getWechatAppSecret() {
        return wechatAppSecret;
    }

    public void setWechatAppSecret(String wechatAppSecret) {
        this.wechatAppSecret = wechatAppSecret;
    }
}
