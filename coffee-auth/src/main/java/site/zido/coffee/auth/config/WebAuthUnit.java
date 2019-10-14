package site.zido.coffee.auth.config;

import site.zido.coffee.auth.web.UrlBasedFilterChainManager;
import site.zido.coffee.auth.web.utils.matcher.AntPathRequestMatcher;
import site.zido.coffee.auth.web.utils.matcher.AnyRequestMatcher;
import site.zido.coffee.auth.web.utils.matcher.RequestMatcher;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zido
 */
public class WebAuthUnit extends
        AbstractConfiguredAuthBuilder<UrlBasedFilterChainManager, WebAuthUnit>
        implements AuthBuilder<UrlBasedFilterChainManager> {
    private List<Filter> filters = new ArrayList<>();
    private RequestMatcher requestMatcher = AnyRequestMatcher.INSTANCE;

    protected WebAuthUnit(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    @Override
    protected UrlBasedFilterChainManager performBuild() throws Exception {
        filters.sort(AnnotationAwareOrderComparator.INSTANCE);
        return new UrlBasedFilterChainManager(requestMatcher, filters);
    }

    public WebAuthUnit requestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
        return this;
    }

    public WebAuthUnit antMatcher(String pattern) {
        return requestMatcher(new AntPathRequestMatcher(pattern));
    }

    public WebAuthUnit addFilter(Filter filter) {
        Class<? extends Filter> filterClass = filter.getClass();
        this.filters.add(filter);
        return this;
    }

    private <C extends AuthConfigurerAdapter<UrlBasedFilterChainManager, WebAuthUnit>> C getOrApply(
            C configurer
    ) throws Exception {
        C config = (C) getConfigurer(configurer.getClass());
        if (config != null) {
            return config;
        }
        return apply(configurer);
    }
}
