package site.zido.coffee.auth.handlers;

import site.zido.coffee.auth.entity.IUser;

public class DaoSessionUserManager extends AbstractSessionUserManager {
    @Override
    protected IUser getUserByKey(Object key) {
        return null;
    }
}
