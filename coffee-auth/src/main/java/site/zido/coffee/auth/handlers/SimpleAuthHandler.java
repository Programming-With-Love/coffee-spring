package site.zido.coffee.auth.handlers;

import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.exceptions.AuthenticationException;
import site.zido.coffee.auth.handlers.AuthHandler;
import site.zido.coffee.auth.handlers.Authenticator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 简单的认证处理器
 *
 * @author zido
 */
public class SimpleAuthHandler implements AuthHandler {
    private Class<? extends IUser> userClass;
    private List<Authenticator> authenticators;

    public SimpleAuthHandler(Class<? extends IUser> userClass, List<Authenticator> authenticators) {
        this.userClass = userClass;
        this.authenticators = authenticators;
    }

    public Class<?> getUserClass() {
        return userClass;
    }

    public void setUserClass(Class<? extends IUser> userClass) {
        this.userClass = userClass;
    }

    @Override
    public IUser attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        for (Authenticator authenticator : authenticators) {
            IUser auth = authenticator.auth(request);
            if (auth != null) {
                return auth;
            }
        }
        return null;
    }
}
