package site.zido.coffee.auth.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

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
public class AuthContextPersistenceFilter extends GenericFilterBean {
    private static final String FILTER_APPLIED = "auth_context_persistence_applied";
    private UserContextRepository repo;

    @Autowired
    public void setRepo(UserContextRepository repo) {
        this.repo = repo;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        Assert.notNull(repo, "user context repository can't be null");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (request.getAttribute(FILTER_APPLIED) != null) {
            // ensure that filter is only applied once per request
            chain.doFilter(request, response);
            return;
        }
        request.setAttribute(FILTER_APPLIED, Boolean.TRUE);
        UserContext userContext = repo.loadContext(request, response);
        try {
            UserHolder.set(userContext);
            chain.doFilter(req, res);
        } finally {
            UserContext contextAfterChainExecution = UserHolder.get();
            UserHolder.clearContext();
            repo.saveContext(contextAfterChainExecution, request, response);
            request.removeAttribute(FILTER_APPLIED);
            logger.debug("user holder now cleared");
        }
    }
}
