package site.zido.coffee.auth.handlers;

import site.zido.coffee.auth.entity.IUser;

import javax.servlet.http.HttpServletRequest;

public interface UserManager {
    IUser getCurrentUser(HttpServletRequest request);
}
