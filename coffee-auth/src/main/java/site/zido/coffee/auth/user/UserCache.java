package site.zido.coffee.auth.user;

/**
 * @author zido
 */
public interface UserCache {
    UserDetails getUserFromCache(String username);

    void putUserInCache(UserDetails user);

    void removeUserFromCache(Object username);
}
