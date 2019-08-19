package site.zido.coffee.auth.web.authentication;

import site.zido.coffee.auth.authentication.AbstractAuthenticationException;
import site.zido.coffee.auth.authentication.UsernamePasswordAuthenticationToken;
import site.zido.coffee.auth.core.Authentication;
import site.zido.coffee.auth.web.AbstractAuthenticationProcessingFilter;
import site.zido.coffee.auth.web.utils.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户名密码认证过滤器
 *
 * @author zido
 */
public class UsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_USERNAME_KEY = "username";
    private static final String DEFAULT_PASSWORD_KEY = "password";

    private String usernameParameter = DEFAULT_USERNAME_KEY;
    private String passwordParameter = DEFAULT_PASSWORD_KEY;

    protected Authentication createToken(HttpServletRequest request, HttpServletResponse response) {
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

    public UsernamePasswordAuthenticationFilter(RequestMatcher entry) {
        super(entry);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AbstractAuthenticationException, IOException, ServletException {
        Authentication authRequest = createToken(request, response);
        return getAuthenticationManager().authenticate(authRequest);
    }
}
