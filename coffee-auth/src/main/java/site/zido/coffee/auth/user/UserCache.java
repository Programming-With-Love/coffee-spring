package site.zido.coffee.auth.user;

/**
 * @author zido
 */
public interface UserCache {
    IUser getUserFromCache(String username);

    void putUserInCache(IUser user);

    void removeUserFromCache(Object username);
}
