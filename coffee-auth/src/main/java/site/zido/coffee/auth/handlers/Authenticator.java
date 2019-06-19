package site.zido.coffee.auth.handlers;

import site.zido.coffee.auth.entity.IUser;

import javax.servlet.http.HttpServletRequest;

public interface Authenticator<T> {
    boolean prepare(Class<T> userClass);

    T auth(HttpServletRequest params);
}
