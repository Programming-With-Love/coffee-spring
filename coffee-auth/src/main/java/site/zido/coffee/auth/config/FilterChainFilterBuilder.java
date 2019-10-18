package site.zido.coffee.auth.config;

import org.springframework.util.Assert;
import site.zido.coffee.auth.web.FilterChainFilter;
import site.zido.coffee.auth.web.FilterChainManager;
import site.zido.coffee.auth.web.HttpSecurityManager;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zido
 */
public class FilterChainFilterBuilder
        extends AbstractConfiguredAuthBuilder<Filter, FilterChainFilterBuilder>
        implements AuthBuilder<Filter> {
    private List<AuthBuilder<? extends FilterChainManager>> filterChainManagerBuilders = new ArrayList<>();
    private HttpSecurityManager securityManager;

    public FilterChainFilterBuilder(ObjectPostProcessor<Object> postProcessor) {
        super(postProcessor);
    }

    @Override
    protected Filter performBuild() throws Exception {
        Assert.state(!filterChainManagerBuilders.isEmpty(), "At least one SecurityBuilder<? extends SecurityFilterChain> needs to be specified. "
                + "Typically this done by adding a @Configuration that extends WebSecurityConfigurerAdapter. "
                + "More advanced users can invoke "
                + FilterChainFilterBuilder.class.getSimpleName()
                + ".addSecurityFilterChainBuilder directly");
        List<FilterChainManager> filterChainManagers = new ArrayList<>(filterChainManagerBuilders.size());
        for (AuthBuilder<? extends FilterChainManager> filterChainManagerBuilder : filterChainManagerBuilders) {
            filterChainManagers.add(filterChainManagerBuilder.build());
        }
        FilterChainFilter filterChainFilter = new FilterChainFilter(filterChainManagers);
        if (securityManager != null) {
            filterChainFilter.setHttpSecurityManager(securityManager);
        }
        filterChainFilter.afterPropertiesSet();
        return filterChainFilter;
    }

    public FilterChainFilterBuilder addFilterChainManagerBuilder(
            AuthBuilder<? extends FilterChainManager> filterChainManagerBuilder) {
        this.filterChainManagerBuilders.add(filterChainManagerBuilder);
        return this;
    }

    public void setSecurityManager(HttpSecurityManager securityManager) {
        this.securityManager = securityManager;
    }
}
