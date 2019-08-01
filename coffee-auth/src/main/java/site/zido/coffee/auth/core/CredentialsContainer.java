package site.zido.coffee.auth.core;

/**
 * 表示实现对象包含敏感数据，例如密码
 * <p>
 * 实现此接口，会由认证器进行敏感数据擦除回调
 *
 * @author zido
 */
public interface CredentialsContainer {
    /**
     * 擦除
     */
    void eraseCredentials();
}
