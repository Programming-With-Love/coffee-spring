package site.zido.coffee.auth.web.authentication;

import org.springframework.util.Assert;
import site.zido.coffee.auth.authentication.AbstractAuthenticationException;
import site.zido.coffee.auth.authentication.AuthenticationTokenFactory;
import site.zido.coffee.auth.authentication.UsernamePasswordAuthenticationToken;
import site.zido.coffee.auth.core.Authentication;
import site.zido.coffee.auth.core.exceptions.NotThisAuthenticatorException;
import site.zido.coffee.auth.web.AbstractAuthenticationProcessingFilter;
import site.zido.coffee.auth.web.utils.matcher.AntPathRequestMatcher;
import site.zido.coffee.auth.web.utils.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * 可配置的认证过滤器
 * <p>
 * 此过滤器主要负责将认证任务分配给所有的凭证构造器
 *
 * @author zido
 */
public class ConfigurableAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private Collection<AuthenticationTokenFactory> tokenFactories;

    public ConfigurableAuthenticationFilter(RequestMatcher entry, List<AuthenticationTokenFactory> tokenFactories) {
        super(entry);
        Assert.notEmpty(tokenFactories, "token factories can't be null or empty");
        if (tokenFactories.contains(null)) {
            throw new IllegalArgumentException("token factories can't contain null element");
        }
        this.tokenFactories = tokenFactories;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AbstractAuthenticationException, IOException, ServletException {
        for (AuthenticationTokenFactory tokenFactory : tokenFactories) {
            Authentication authRequest = tokenFactory.createToken(request, response);
            if (authRequest != null) {
                return getAuthenticationManager().authenticate(authRequest);
            }
        }
        throw new NotThisAuthenticatorException();
    }
}
