package site.zido.coffee.auth.user;

public class NullUserCache implements UserCache {
    @Override
    public UserDetails getUserFromCache(String username) {
        return null;
    }

    @Override
    public void putUserInCache(UserDetails user) {

    }

    @Override
    public void removeUserFromCache(String username) {

    }
}
