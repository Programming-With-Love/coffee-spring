package site.zido.coffee.auth.handlers;

import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.exceptions.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

public interface AuthHandler<T extends IUser, Key extends Serializable> {
    T getUserByKey(Key key);

    T attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException;
}
