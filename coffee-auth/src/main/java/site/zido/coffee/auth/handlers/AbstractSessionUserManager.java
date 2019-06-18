package site.zido.coffee.auth.handlers;

import site.zido.coffee.auth.entity.IUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Collections;

public abstract class AbstractSessionUserManager implements UserManager {
    private String sessionKey = "key";

    protected abstract IUser getUserByKey(Object key);

    @Override
    public IUser getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object key = session.getAttribute(sessionKey);
        if (key == null) {
            return null;
        }
        //TODO
        return null;
    }

    @Override
    public Collection<String> getRoles(IUser user) {
        return Collections.singleton(user.role());
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }
}
