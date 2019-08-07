package site.zido.coffee.auth.web.session;

import site.zido.coffee.auth.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 不安全的，啥都不做
 *
 * @author zido
 */
public class NotSecuritySessionStrategy implements SecuritySessionStrategy {
    @Override
    public void onAuthentication(Authentication authentication, HttpServletRequest request, HttpServletResponse response) throws SessionAuthenticationException {
    }
}
