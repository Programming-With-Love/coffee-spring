package site.zido.coffee.auth.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import site.zido.coffee.auth.CoffeeAuthAutoConfiguration;
import site.zido.coffee.auth.authentication.logout.LogoutFilter;
import site.zido.coffee.auth.authentication.logout.LogoutSuccessHandler;
import site.zido.coffee.auth.context.AuthContextPersistenceFilter;
import site.zido.coffee.auth.user.annotations.AuthEntity;
import site.zido.coffee.auth.web.FilterChainFilter;
import site.zido.coffee.auth.web.FilterChainManager;
import site.zido.coffee.auth.web.HttpSecurityManager;
import site.zido.coffee.auth.web.UrlBasedFilterChainManager;
import site.zido.coffee.auth.web.utils.matcher.AntPathRequestMatcher;
import site.zido.coffee.auth.web.utils.matcher.OrRequestMatcher;
import site.zido.coffee.auth.web.utils.matcher.RequestMatcher;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilterChainFilterFactoryBean implements FactoryBean<FilterChainFilter>, ApplicationContextAware {
    private ObjectPostProcessor<Object> objectObjectPostProcessor;
    private HttpSecurityManager securityManager;
    private ApplicationContext context;
    private AuthClassAutoRegistrar registrar;

    public FilterChainFilterFactoryBean(ObjectPostProcessor<Object> objectObjectPostProcessor) {
        this.objectObjectPostProcessor = objectObjectPostProcessor;
    }

    public FilterChainFilter buildFilterChainFilter() {
        List<FilterChainManager> managers = new ArrayList<>();
        Map<String, AuthenticationFilterFactory> authenticationFilterFactoryMap = BeanFactoryUtils.beansOfTypeIncludingAncestors(context,
                AuthenticationFilterFactory.class);
        for (String className : registrar.getAuthClassNames()) {
            Class<?> clazz;
            try {
                clazz = CoffeeAuthAutoConfiguration.class.getClassLoader().loadClass(className);
            } catch (ClassNotFoundException ignore) {
                //not reachable
                continue;
            }
            AuthEntity authEntity = AnnotatedElementUtils
                    .findMergedAnnotation(clazz, AuthEntity.class);
            String[] baseUrls = authEntity.baseUrl();
            List<Filter> filters = new ArrayList<>(3);
            //认证上下文持久化过滤器
            AuthContextPersistenceFilter beforeFilter = objectObjectPostProcessor
                    .postProcess(new AuthContextPersistenceFilter());
            filters.add(beforeFilter);
            for (AuthenticationFilterFactory factory : authenticationFilterFactoryMap.values()) {
                Filter filter = factory.createFilter(clazz, objectObjectPostProcessor);
                if (filter != null) {
                    filters.add(filter);
                }
            }

            filters.sort(AnnotationAwareOrderComparator.INSTANCE);
            List<RequestMatcher> baseUrlMatchers = Stream.of(baseUrls).map(baseUrl -> {
                if (!baseUrl.startsWith("/")) {
                    baseUrl = "/" + baseUrl;
                }
                return new AntPathRequestMatcher(baseUrl);
            }).collect(Collectors.toList());
            OrRequestMatcher baseRequestMatcher = new OrRequestMatcher(baseUrlMatchers);
            UrlBasedFilterChainManager urlBasedFilterChainManager = objectObjectPostProcessor
                    .postProcess(new UrlBasedFilterChainManager(baseRequestMatcher, filters));
            managers.add(urlBasedFilterChainManager);
        }
        FilterChainFilter filterChainFilter = objectObjectPostProcessor.postProcess(new FilterChainFilter());
        filterChainFilter.setFilterChainManagers(managers);
        if (securityManager != null) {
            filterChainFilter.setHttpSecurityManager(securityManager);
        }
        return filterChainFilter;
    }

    @Autowired(required = false)
    public void setSecurityManager(HttpSecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Autowired
    public void setRegistrar(AuthClassAutoRegistrar registrar) {
        this.registrar = registrar;
    }

    @Override
    public FilterChainFilter getObject() throws Exception {
        return buildFilterChainFilter();
    }

    @Override
    public Class<?> getObjectType() {
        return FilterChainFilter.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
