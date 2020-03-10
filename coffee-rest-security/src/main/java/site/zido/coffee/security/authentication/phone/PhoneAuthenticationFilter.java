package site.zido.coffee.security.authentication.phone;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import site.zido.coffee.common.validations.PhoneValidator;

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
public class PhoneAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private RequestMatcher codeRequestMatcher = new AntPathRequestMatcher("/phone/code", "POST");
    private PhoneValidator phoneValidator = new PhoneValidator();
    private PhoneCodeService phoneCodeService;
    private CodeGenerator codeGenerator = new CustomCodeGenerator(CustomCodeGenerator.Mode.NUMBER);
    private PhoneCodeCache cache;
    private String cachePrefix = "";
    private String phoneParameter = "phone";
    private String codeParameter = "code";

    public PhoneAuthenticationFilter() {
        super(new AntPathRequestMatcher("/phone/sessions", "POST"));
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        String phone = obtainPhone(request);
        if (requireCreateCode(request)) {
            if (phone == null || !phoneValidator.isValid(phone, null)) {
                throw new BadCredentialsException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.badCredentials",
                        "Bad phone"));
            }
            String code = codeGenerator.generateCode(phone);
            phoneCodeService.sendCode(phone, code);
            cache.put(getCacheKey(phone), code);
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


    protected String obtainPhone(HttpServletRequest request) {
        return request.getParameter(phoneParameter);
    }

    protected String getCacheKey(String phone) {
        return cachePrefix + phone;
    }

    public void setPhoneParameter(String phone) {
        Assert.hasLength(phone, "phone parameter cannot be null or empty");
        this.phoneParameter = phone;
    }

    public void setCodeParameter(String codeParameter) {
        Assert.hasLength(codeParameter, "code parameter cannot be null or empty");
        this.codeParameter = codeParameter;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        String phone = obtainPhone(request);
        if (phone == null) {
            phone = "";
        }
        String code = obtainCode(request);
        if (code == null) {
            code = "";
        }
        PhoneCodeAuthenticationToken authRequest = new PhoneCodeAuthenticationToken(phone, code);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request,
                              PhoneCodeAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Assert.notNull(cache, "cache cannot be null");
    }

    public void setCachePrefix(String cachePrefix) {
        this.cachePrefix = cachePrefix;
    }

    public void setCache(PhoneCodeCache cache) {
        this.cache = cache;
    }

    public void setCodeGenerator(CodeGenerator codeGenerator) {
        this.codeGenerator = codeGenerator;
    }

    public void setPhoneCodeService(PhoneCodeService phoneCodeService) {
        this.phoneCodeService = phoneCodeService;
    }

    public RequestMatcher getCodeRequestMatcher() {
        return codeRequestMatcher;
    }
}
