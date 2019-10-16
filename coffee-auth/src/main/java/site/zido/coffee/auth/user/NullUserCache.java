package site.zido.coffee.auth.user;

public class NullUserCache implements UserCache {
    @Override
    public IUser getUserFromCache(String username) {
        return null;
    }

    @Override
    public void putUserInCache(IUser user) {

    }

    @Override
    public void removeUserFromCache(Object username) {

    }
}
