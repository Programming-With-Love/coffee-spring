package site.zido.coffee.auth.handlers;

import site.zido.coffee.auth.entity.IUser;

import java.util.Map;

public interface Authenticator<T extends IUser> {
    boolean prepare(Class<? extends IUser> userClass);

    T auth(Map<String, String> params);
}
