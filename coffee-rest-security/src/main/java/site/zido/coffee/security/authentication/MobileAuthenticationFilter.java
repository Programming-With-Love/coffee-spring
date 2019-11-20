package site.zido.coffee.security.authentication;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import site.zido.coffee.security.managers.MobileCodeManager;
import site.zido.coffee.security.validations.MobileValidator;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zido
 */
public class MobileAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private RequestMatcher codeRequestMatcher = new AntPathRequestMatcher("/mobile/code", "POST");
    private MobileValidator mobileValidator = new MobileValidator();
    private MobileCodeManager mobileCodeManager;
    private String mobileParameter = "mobile";
    private String codeParameter = "code";

    public MobileAuthenticationFilter(MobileCodeManager mobileCodeManager) {
        super(new AntPathRequestMatcher("/mobile/sessions", "POST"));
        this.mobileCodeManager = mobileCodeManager;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String mobile = obtainMobile(request);
        if (requireCreateCode(request)) {
            if (mobile == null || !mobileValidator.isValid(mobile, null)) {
                throw new BadCredentialsException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.badCredentials",
                        "Bad mobile"));
            }
            mobileCodeManager.sendCode(mobile);
            chain.doFilter(request, response);
            return;
        }
        super.doFilter(req, res, chain);
    }

    private String obtainCode(HttpServletRequest request) {
        return request.getParameter(codeParameter);
    }

    private boolean requireCreateCode(HttpServletRequest request) {
        return codeRequestMatcher.matches(request);
    }

    public void setCodeRequestMatcher(RequestMatcher codeRequestMatcher) {
        Assert.notNull(codeRequestMatcher, "send code request matcher cannot be null");
        this.codeRequestMatcher = codeRequestMatcher;
    }


    protected String obtainMobile(HttpServletRequest request) {
        return request.getParameter(mobileParameter);
    }

    public void setMobileParameter(String mobileParameter) {
        Assert.hasLength(mobileParameter, "mobile parameter cannot be null or empty");
        this.mobileParameter = mobileParameter;
    }

    public void setCodeParameter(String codeParameter) {
        Assert.hasLength(codeParameter, "code parameter cannot be null or empty");
        this.codeParameter = codeParameter;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        String mobile = obtainMobile(request);
        if (mobile == null) {
            mobile = "";
        }
        String code = obtainCode(request);
        if (code == null) {
            code = "";
        }
        MobileCodeAuthenticationToken authRequest = new MobileCodeAuthenticationToken(mobile, code);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request,
                              UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
    }
}
