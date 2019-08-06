package site.zido.coffee.auth.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.zido.coffee.auth.web.utils.matcher.RequestMatcher;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * 默认过滤器链的管理器
 *
 * @author zido
 */
public class DefaultFilterChainManager implements FilterChainManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFilterChainManager.class);
    private final List<Filter> filters;
    private final RequestMatcher requestMatcher;

    public DefaultFilterChainManager(RequestMatcher requestMatcher, Filter... filters) {
        this(requestMatcher, Arrays.asList(filters));
    }

    public DefaultFilterChainManager(RequestMatcher requestMatcher, List<Filter> filters) {
        LOGGER.info("Creating filter chain: " + requestMatcher + "," + filters);
        this.filters = filters;
        this.requestMatcher = requestMatcher;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return requestMatcher.matches(request);
    }

    @Override
    public List<Filter> getFilters() {
        return filters;
    }

    @Override
    public String toString() {
        return "[ " + requestMatcher + ", " + filters + "]";
    }
}
