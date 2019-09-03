package site.zido.coffee.auth.authentication.logout;

import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;
import site.zido.coffee.auth.context.UserHolder;
import site.zido.coffee.auth.core.Authentication;
import site.zido.coffee.auth.web.utils.matcher.AntPathRequestMatcher;
import site.zido.coffee.auth.web.utils.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutFilter extends GenericFilterBean {
    private RequestMatcher logoutRequestMatcher;
    private final LogoutHandler handler;
    private final LogoutSuccessHandler logoutSuccessHandler;

    public LogoutFilter(LogoutSuccessHandler logoutSuccessHandler,
                        LogoutHandler... handlers) {
        this.handler = new CompositeLogoutHandler(handlers);
        Assert.notNull(logoutSuccessHandler, "logoutSuccessHandler cannot be null");
        this.logoutSuccessHandler = logoutSuccessHandler;
        setFilterProcessesUrl("/logout");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        if (requiresLogout(request, response)) {
            Authentication auth = UserHolder.get().getAuthentication();

            if (logger.isDebugEnabled()) {
                logger.debug("Logging out user '" + auth
                        + "' and transferring to logout destination");
            }

            this.handler.logout(request, response, auth);

            logoutSuccessHandler.onLogoutSuccess(request, response, auth);

            return;
        }

        chain.doFilter(request, response);
    }

    protected boolean requiresLogout(HttpServletRequest request,
                                     HttpServletResponse response) {
        return logoutRequestMatcher.matches(request);
    }

    public void setLogoutRequestMatcher(RequestMatcher logoutRequestMatcher) {
        this.logoutRequestMatcher = logoutRequestMatcher;
    }

    public void setFilterProcessesUrl(String filterProcessesUrl) {
        this.logoutRequestMatcher = new AntPathRequestMatcher(filterProcessesUrl);
    }
}
