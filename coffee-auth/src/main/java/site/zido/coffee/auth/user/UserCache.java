package site.zido.coffee.auth.user;

import site.zido.coffee.auth.user.UserDetails;

public interface UserCache {
    UserDetails getUserFromCache(String username);

    void putUserInCache(UserDetails user);

    void removeUserFromCache(Object username);
}
