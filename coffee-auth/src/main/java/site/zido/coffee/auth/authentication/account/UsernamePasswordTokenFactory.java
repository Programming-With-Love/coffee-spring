package site.zido.coffee.auth.authentication.account;

import org.springframework.http.HttpMethod;
import site.zido.coffee.auth.authentication.AuthenticationTokenFactory;
import site.zido.coffee.auth.authentication.InternalAuthenticationException;
import site.zido.coffee.auth.authentication.UsernamePasswordAuthenticationToken;
import site.zido.coffee.auth.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户名密码认证支持
 *
 * @author zido
 */
public class UsernamePasswordTokenFactory implements AuthenticationTokenFactory {
    private static final String DEFAULT_USERNAME_KEY = "username";
    private static final String DEFAULT_PASSWORD_KEY = "password";

    private String usernameParameter = DEFAULT_USERNAME_KEY;
    private String passwordParameter = DEFAULT_PASSWORD_KEY;
    private boolean postOnly = true;

    @Override
    public Authentication createToken(HttpServletRequest request, HttpServletResponse response) {
        if (postOnly && !"POST".equals(request.getMethod())) {
            throw new InternalAuthenticationException(
                    "Authentication method not supported: " + request.getMethod());
        }

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        }

        username = username.trim();

        return new UsernamePasswordAuthenticationToken(
                username, password);
    }

    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter(passwordParameter);
    }

    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(usernameParameter);
    }

    public void setUsernameParameter(String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        this.passwordParameter = passwordParameter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }
}
