package site.zido.coffee.auth.handlers;

import site.zido.coffee.auth.entity.IUser;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

public interface UserManager {
    IUser getCurrentUser(HttpServletRequest request);

    Collection<String> getRoles(IUser user);

    void setUser(HttpServletRequest request, IUser authResult);
}
