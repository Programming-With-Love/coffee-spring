package site.zido.coffee.auth.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;
import site.zido.coffee.auth.context.SecurityContextHolder;
import site.zido.coffee.auth.utils.UrlUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FilterChainProxy extends GenericFilterBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterChainProxy.class);

    private static final String FILTER_APPLIED = FilterChainProxy.class.getName().concat(".APPLIED");

    private List<SecurityFilterChain> filterChains;


    public FilterChainProxy() {
    }

    public FilterChainProxy(SecurityFilterChain chain) {
        this(Arrays.asList(chain));
    }

    public FilterChainProxy(List<SecurityFilterChain> filterChains) {
        this.filterChains = filterChains;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        boolean clearContext = request.getAttribute(FILTER_APPLIED) == null;
        if (clearContext) {
            try {
                request.setAttribute(FILTER_APPLIED, Boolean.TRUE);
                doFilterInternal(request, response, filterChain);
            } finally {
                SecurityContextHolder.clearContext();
                request.removeAttribute(FILTER_APPLIED);
            }
        } else {
            doFilterInternal(request, response, filterChain);
        }
    }

    private void doFilterInternal(ServletRequest servletRequest, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        List<Filter> filters = getFilters(request);
        if (filters == null || filters.size() == 0) {
            LOGGER.debug(UrlUtils.buildRequestUrl(request) + (filters == null
                    ? " has no matching filters"
                    : " has empty filter list"));
            filterChain.doFilter(request, response);
            return;
        }
        VirtualFilterChain vfc = new VirtualFilterChain(request, filterChain, filters);
        vfc.doFilter(request, response);
    }

    private List<Filter> getFilters(HttpServletRequest request) {
        for (SecurityFilterChain filterChain : filterChains) {
            if (filterChain.matches(request)) {
                return filterChain.getFilters();
            }
        }
        return null;
    }

    private static class VirtualFilterChain implements FilterChain {
        private final FilterChain originalChain;
        private final List<Filter> additionalFilters;
        private final HttpServletRequest originalRequest;
        private final int size;
        private int currentPosition = 0;

        private VirtualFilterChain(HttpServletRequest request,
                                   FilterChain chain, List<Filter> additionalFilters) {
            this.originalChain = chain;
            this.additionalFilters = additionalFilters;
            this.size = additionalFilters.size();
            this.originalRequest = request;
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            if (currentPosition == size) {
                LOGGER.debug("{}  reached end of additional filter chain; proceeding with original chain", UrlUtils.buildRequestUrl(originalRequest));
                originalChain.doFilter(request, response);
            } else {
                currentPosition++;
                Filter next = additionalFilters.get(currentPosition - 1);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(UrlUtils.buildRequestUrl(originalRequest)
                            + " at position " + currentPosition + " of " + size
                            + " in additional filter chain; firing Filter: '"
                            + next.getClass().getSimpleName() + "'");
                }
                next.doFilter(request, response, this);
            }
        }
    }
}
