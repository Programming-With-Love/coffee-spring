package site.zido.coffee.auth.authentication;

import site.zido.coffee.auth.entity.IUser;

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

    /**
     * 简单的遍历各自的认证器
     *
     * @param request  request
     * @param response response
     * @return 用户
     * @throws AbstractAuthenticationException ex
     */
    @Override
    public IUser attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AbstractAuthenticationException {
        for (Authenticator authenticator : authenticators) {
            IUser auth = authenticator.auth(request);
            if (auth != null) {
                return auth;
            }
        }
        return null;
    }
}
