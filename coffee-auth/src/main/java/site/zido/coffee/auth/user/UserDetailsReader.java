package site.zido.coffee.auth.user;

/**
 * 用户信息读取器,用于将任意用户对象解析为UserDetails接口，以帮助进行用户相关属性读取
 *
 * @author zido
 */
public interface UserDetailsReader<T extends IUser> {
    /**
     * 解析用户对象
     *
     * @param user 任意用户对象
     * @return user details
     */
    T parseUser(Object user);
}
