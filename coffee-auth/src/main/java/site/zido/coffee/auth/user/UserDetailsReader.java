package site.zido.coffee.auth.user;

/**
 * 用户信息读取器,用于将任意用户对象解析为UserDetails接口，以帮助进行用户相关属性读取
 *
 * @author zido
 */
public interface UserDetailsReader {
    /**
     * 解析用户对象
     *
     * @param user 任意用户对象
     * @return user details
     */
    UserDetails parseUser(Object user);
}
