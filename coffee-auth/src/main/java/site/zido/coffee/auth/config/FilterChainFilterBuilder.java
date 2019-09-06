package site.zido.coffee.auth.config;

import org.springframework.util.Assert;
import site.zido.coffee.auth.web.FilterChainFilter;
import site.zido.coffee.auth.web.FilterChainManager;
import site.zido.coffee.auth.web.HttpSecurityManager;

import java.util.ArrayList;
import java.util.List;

public class FilterChainFilterBuilder {
    private ObjectPostProcessor<Object> objectPostProcessor;
    private List<FilterChainManager> filterChainManagers;
    private HttpSecurityManager securityManager;

    public FilterChainFilterBuilder(ObjectPostProcessor<Object> postProcessor) {
        Assert.notNull(postProcessor, "objectPostProcessor cannot be null");
        this.objectPostProcessor = postProcessor;
    }

    public FilterChainFilter build() {
        FilterChainFilter filterChainFilter = objectPostProcessor.postProcess(new FilterChainFilter());
        filterChainFilter.setFilterChainManagers(filterChainManagers);
        if (securityManager == null) {
            filterChainFilter.setHttpSecurityManager(securityManager);
        }
        return filterChainFilter;
    }

    public FilterChainFilterBuilder addFilterChainManager(FilterChainManager manager) {
        if (filterChainManagers == null) {
            filterChainManagers = new ArrayList<>();
        }
        filterChainManagers.add(manager);
        return this;
    }

    public void setSecurityManager(HttpSecurityManager securityManager) {
        this.securityManager = securityManager;
    }
}
