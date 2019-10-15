package site.zido.coffee.auth.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;
import site.zido.coffee.auth.context.UserHolder;
import site.zido.coffee.auth.web.utils.UrlUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
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
    private List<FilterChainManager> filterChainManagers;

    public FilterChainFilter() {
    }

    public FilterChainFilter(List<FilterChainManager> managers) {
        Assert.notEmpty(managers, "managers cannot be empty or null");
        for (FilterChainManager manager : managers) {
            Assert.notNull(manager, "managers cannot contain null manager");
        }
        this.filterChainManagers = managers;
    }

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
        List<Filter> filters = getFilters(seRequest);

        if (filters == null || filters.size() == 0) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(UrlUtils.buildRequestUrl(seRequest)
                        + (filters == null ? " has no matching filters"
                        : " has an empty filter list"));
            }
            chain.doFilter(seRequest, seResponse);
            return;
        }
        VirtualFilterChain vfc = new VirtualFilterChain(seRequest, chain, filters);
        vfc.doFilter(seRequest, seResponse);
    }

    private List<Filter> getFilters(HttpServletRequest request) {
        for (FilterChainManager manager : filterChainManagers) {
            if (manager.matches(request)) {
                return manager.getFilters();
            }
        }
        return null;
    }

    public void setHttpSecurityManager(HttpSecurityManager httpSecurityManager) {
        this.httpSecurityManager = httpSecurityManager;
    }

    public List<FilterChainManager> getFilterChainManagers() {
        return Collections.unmodifiableList(filterChainManagers);
    }

    public void setFilterChainManagers(List<FilterChainManager> filterChainManagers) {
        this.filterChainManagers = filterChainManagers;
    }

    private static class VirtualFilterChain implements FilterChain {
        private final FilterChain originalChain;
        private final List<Filter> additionalFilters;
        private final HttpServletRequest firewalledRequest;
        private final int size;
        private int currentPosition = 0;

        private VirtualFilterChain(HttpServletRequest firewalledRequest,
                                   FilterChain chain, List<Filter> additionalFilters) {
            this.originalChain = chain;
            this.additionalFilters = additionalFilters;
            this.size = additionalFilters.size();
            this.firewalledRequest = firewalledRequest;
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response)
                throws IOException, ServletException {
            if (currentPosition == size) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(UrlUtils.buildRequestUrl(firewalledRequest)
                            + " reached end of additional filter chain; proceeding with original chain");
                }

                originalChain.doFilter(request, response);
            } else {
                currentPosition++;

                Filter nextFilter = additionalFilters.get(currentPosition - 1);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(UrlUtils.buildRequestUrl(firewalledRequest)
                            + " at position " + currentPosition + " of " + size
                            + " in additional filter chain; firing Filter: '"
                            + nextFilter.getClass().getSimpleName() + "'");
                }

                nextFilter.doFilter(request, response, this);
            }
        }
    }
}
