package site.zido.coffee.auth.configurations;

/**
 * 实体关联的相关属性
 */
public class EntityProps {
    private FieldWrapper username;
    private FieldWrapper password;
    private FieldWrapper weChatOpenId;
    private FieldWrapper weChatUnionId;
    private FieldWrapper mobile;

    public FieldWrapper getUsername() {
        return username;
    }

    public void setUsername(FieldWrapper username) {
        this.username = username;
    }

    public FieldWrapper getPassword() {
        return password;
    }

    public void setPassword(FieldWrapper password) {
        this.password = password;
    }

    public FieldWrapper getWeChatOpenId() {
        return weChatOpenId;
    }

    public void setWeChatOpenId(FieldWrapper weChatOpenId) {
        this.weChatOpenId = weChatOpenId;
    }

    public FieldWrapper getWeChatUnionId() {
        return weChatUnionId;
    }

    public void setWeChatUnionId(FieldWrapper weChatUnionId) {
        this.weChatUnionId = weChatUnionId;
    }

    public FieldWrapper getMobile() {
        return mobile;
    }

    public void setMobile(FieldWrapper mobile) {
        this.mobile = mobile;
    }
}
