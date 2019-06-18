package site.zido.coffee.auth.handlers;

import site.zido.coffee.auth.entity.IUser;

public interface AuthHandler {
    IUser getUserByKey(Object key);
}
