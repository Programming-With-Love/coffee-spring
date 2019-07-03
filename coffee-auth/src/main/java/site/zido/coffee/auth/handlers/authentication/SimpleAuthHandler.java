package site.zido.coffee.auth.handlers.authentication;

import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.exceptions.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 简单的认证处理器
 *
 * @author zido
 */
public class SimpleAuthHandler implements AuthHandler {
    private List<Authenticator> authenticators;

    public SimpleAuthHandler(List<Authenticator> authenticators) {
        this.authenticators = authenticators;
    }

    @Override
    public IUser attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        for (Authenticator authenticator : authenticators) {
            IUser auth = authenticator.auth(request);
            if (auth != null) {
                return auth;
            }
        }
        return null;
    }
}
