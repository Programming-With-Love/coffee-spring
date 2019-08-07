package site.zido.coffee.auth.web.authentication;

import site.zido.coffee.auth.authentication.AbstractAuthenticationException;
import site.zido.coffee.auth.authentication.UsernamePasswordAuthenticationToken;
import site.zido.coffee.auth.core.Authentication;
import site.zido.coffee.auth.web.AbstractAuthenticationProcessingFilter;
import site.zido.coffee.auth.web.utils.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zido
 */
public class UsernamePasswordFilter extends AbstractAuthenticationProcessingFilter {

    public static final String DEFAULT_USERNAME_KEY = "username";
    private static final String DEFAULT_PASSWORD_KEY = "password";

    private String usernameParameter = DEFAULT_USERNAME_KEY;
    private String passwordParameter = DEFAULT_PASSWORD_KEY;
    private boolean postOnly = true;

    protected UsernamePasswordFilter() {
        super(new AntPathRequestMatcher("/login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AbstractAuthenticationException, IOException, ServletException {
        String username = request.getParameter(usernameParameter);
        String password = request.getParameter(passwordParameter);
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        username = username.trim();
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
