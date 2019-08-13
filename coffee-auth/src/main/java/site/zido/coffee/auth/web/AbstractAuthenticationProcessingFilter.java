package site.zido.coffee.auth.web;

import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;
import site.zido.coffee.auth.authentication.AbstractAuthenticationException;
import site.zido.coffee.auth.authentication.AuthenticationManager;
import site.zido.coffee.auth.authentication.InternalAuthenticationException;
import site.zido.coffee.auth.context.UserHolder;
import site.zido.coffee.auth.core.Authentication;
import site.zido.coffee.auth.handlers.LoginFailureHandler;
import site.zido.coffee.auth.handlers.LoginSuccessHandler;
import site.zido.coffee.auth.web.authentication.NullRememberMeService;
import site.zido.coffee.auth.web.authentication.RememberMeService;
import site.zido.coffee.auth.web.session.NotSecuritySessionStrategy;
import site.zido.coffee.auth.web.session.SecuritySessionStrategy;
import site.zido.coffee.auth.web.utils.matcher.AntPathRequestMatcher;
import site.zido.coffee.auth.web.utils.matcher.RequestMatcher;

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
public abstract class AbstractAuthenticationProcessingFilter extends GenericFilterBean {
    private RequestMatcher requestMatcher;
    private boolean continueChainBeforeSuccessfulAuthentication = false;
    private SecuritySessionStrategy sessionStrategy = new NotSecuritySessionStrategy();
    private LoginSuccessHandler successHandler;
    private LoginFailureHandler failureHandler;
    private RememberMeService rememberMeService = new NullRememberMeService();
    private AuthenticationManager authenticationManager;

    protected AbstractAuthenticationProcessingFilter(
            RequestMatcher requestMatcher) {
        Assert.notNull(requestMatcher,
                "requestMatcher cannot be null");
        this.requestMatcher = requestMatcher;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        if (!requiresAuthentication(request, response)) {
            chain.doFilter(request, response);
            return;
        }
        logger.debug("Request is to process authentication");
        Authentication authResult;
        try {
            authResult = attemptAuthentication(request, response);
            if (authResult == null) {
                return;
            }
            sessionStrategy.onAuthentication(authResult, request, response);
        } catch (InternalAuthenticationException e) {
            logger.error("An internal error occurred while trying to authenticate the user.", e);
            unsuccessfulAuthentication(request, response, e);
            return;
        } catch (AbstractAuthenticationException e) {
            unsuccessfulAuthentication(request, response, e);
            return;
        }
        if (continueChainBeforeSuccessfulAuthentication) {
            chain.doFilter(request, response);
        }
        successfulAuthentication(request, response, chain, authResult);
    }

    protected boolean requiresAuthentication(HttpServletRequest request,
                                             HttpServletResponse response) {
        return requestMatcher.matches(request);
    }

    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException, ServletException {

        if (logger.isDebugEnabled()) {
            logger.debug("Authentication success. Updating SecurityContextHolder to contain: "
                    + authResult);
        }
        UserHolder.get().setAuthentication(authResult);
        rememberMeService.loginSuccess(request, response, authResult);
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response, AbstractAuthenticationException failed)
            throws IOException, ServletException {
        UserHolder.clearContext();

        if (logger.isDebugEnabled()) {
            logger.debug("Authentication request failed: " + failed.toString(), failed);
            logger.debug("Updated SecurityContextHolder to contain null Authentication");
            logger.debug("Delegating to authentication failure handler " + failureHandler);
        }

        rememberMeService.loginFail(request, response);

        failureHandler.onAuthenticationFailure(request, response, failed);
    }

    public void setAuthenticationManager(AuthenticationManager authenticationmanager) {
        this.authenticationManager = authenticationmanager;
    }

    public void setRequestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }

    public void setFilterProcessesUrl(String filterProcessesUrl) {
        setRequestMatcher(new AntPathRequestMatcher(
                filterProcessesUrl));
    }

    public LoginSuccessHandler getSuccessHandler() {
        return successHandler;
    }

    public void setSuccessHandler(LoginSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    public LoginFailureHandler getFailureHandler() {
        return failureHandler;
    }

    public void setFailureHandler(LoginFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setContinueChainBeforeSuccessfulAuthentication(boolean continueChainBeforeSuccessfulAuthentication) {
        this.continueChainBeforeSuccessfulAuthentication = continueChainBeforeSuccessfulAuthentication;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        Assert.notNull(authenticationManager, "authenticationManager can't be null");
    }

    public abstract Authentication attemptAuthentication(HttpServletRequest request,
                                                         HttpServletResponse response)
            throws AbstractAuthenticationException, IOException, ServletException;
}
