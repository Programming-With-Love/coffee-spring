package site.zido.coffee.auth.handlers;

import site.zido.coffee.auth.entity.IUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

public interface AuthHandler<T extends IUser, Key extends Serializable> {
    T getUserByKey(Key key);

    T attempAuthentication(HttpServletRequest request, HttpServletResponse response);
}
