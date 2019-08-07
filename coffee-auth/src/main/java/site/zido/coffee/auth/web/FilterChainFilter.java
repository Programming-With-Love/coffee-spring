package site.zido.coffee.auth.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;
import site.zido.coffee.auth.context.UserHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 过滤器链的过滤器，用于遍历安全认证过滤器链
 *
 * @author zido
 */
public class FilterChainFilter extends GenericFilterBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterChainFilter.class);
    private final static String FILTER_APPLIED = FilterChainFilter.class.getName().concat(
            ".APPLIED");
    private HttpSecurityManager httpSecurityManager = new StrictHttpSecurityManager();


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request.getAttribute(FILTER_APPLIED) == null) {
            try {
                request.setAttribute(FILTER_APPLIED, true);
                doFilterInternal(request, response, chain);
            } finally {
                UserHolder.clearContext();
                request.removeAttribute(FILTER_APPLIED);
            }
        } else {
            doFilterInternal(request, response, chain);
        }
    }

    private void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest seRequest = httpSecurityManager.getSecurityRequest((HttpServletRequest) request);
        HttpServletResponse seResponse = httpSecurityManager.getSecurityResponse((HttpServletResponse) response);
        getFilters(seRequest);
        //TODO do filter internal in filter chain filter
    }

    private List<Filter> getFilters(HttpServletRequest request) {
        return null;
    }

    public void setHttpSecurityManager(HttpSecurityManager httpSecurityManager) {
        this.httpSecurityManager = httpSecurityManager;
    }
}
