package site.zido.coffee.auth.user;

/**
 * 用户名密码登陆用户支持接口
 *
 * @author zido
 */
public interface UsernamePasswordUser {

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    String getUsername();

    /**
     * 获取密码
     *
     * @return 密码
     */
    String getPassword();
}
