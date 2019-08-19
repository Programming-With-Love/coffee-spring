package site.zido.coffee.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.web.util.UrlPathHelper;
import site.zido.coffee.auth.authentication.AuthenticationProvider;
import site.zido.coffee.auth.authentication.AuthenticationTokenFactory;
import site.zido.coffee.auth.authentication.ProviderManager;
import site.zido.coffee.auth.authentication.account.UsernamePasswordTokenFactory;
import site.zido.coffee.auth.config.DefaultAuthBeforeUserFiltersFactory;
import site.zido.coffee.auth.config.ObjectPostProcessor;
import site.zido.coffee.auth.config.UserFiltersFactory;
import site.zido.coffee.auth.context.AuthContextPersistenceFilter;
import site.zido.coffee.auth.context.HttpSessionUserContextRepository;
import site.zido.coffee.auth.context.UserContextRepository;
import site.zido.coffee.auth.user.annotations.AuthEntity;
import site.zido.coffee.auth.web.FilterChainFilter;
import site.zido.coffee.auth.web.FilterChainManager;
import site.zido.coffee.auth.web.HttpSecurityManager;
import site.zido.coffee.auth.web.UrlBasedFilterChainManager;
import site.zido.coffee.auth.web.authentication.ConfigurableAuthenticationFilter;
import site.zido.coffee.auth.web.utils.matcher.AntPathRequestMatcher;
import site.zido.coffee.auth.web.utils.matcher.OrRequestMatcher;
import site.zido.coffee.auth.web.utils.matcher.RequestMatcher;

import javax.servlet.Filter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 认证相关自动配置
 *
 * @author zido
 */
@Configuration
@AutoConfigureAfter(JpaRepositoriesAutoConfiguration.class)
@Import(AuthCommonConfiguration.class)
public class CoffeeAuthAutoConfiguration implements BeanFactoryAware {
    private static Logger LOGGER = LoggerFactory.getLogger(CoffeeAuthAutoConfiguration.class);
    private List<String> authClassNames;
    private BeanFactory beanFactory;

    @Autowired(required = false)
    public void setUnitInfo(MutablePersistenceUnitInfo unitInfo) {
        authClassNames = new ArrayList<>();
        List<String> entityClassNames = unitInfo.getManagedClassNames();
        for (String entityClassName : entityClassNames) {
            Class<?> clazz = null;
            try {
                clazz = CoffeeAuthAutoConfiguration.class.getClassLoader().loadClass(entityClassName);
            } catch (ClassNotFoundException e) {
                LOGGER.error("can't load " + entityClassName, e);
                continue;
            }
            AuthEntity authEntity = AnnotatedElementUtils.findMergedAnnotation(clazz, AuthEntity.class);
            if (authEntity != null) {
                authClassNames.add(entityClassName);
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean(DefaultAuthBeforeUserFiltersFactory.class)
    public UserFiltersFactory beforeUserFiltersFactory() {
        return new DefaultAuthBeforeUserFiltersFactory();
    }

    @Bean
    @ConditionalOnMissingBean(UserContextRepository.class)
    public HttpSessionUserContextRepository userContextRepository() {
        return new HttpSessionUserContextRepository();
    }

    @Bean
    @ConditionalOnMissingBean(UsernamePasswordTokenFactory.class)
    public AuthenticationTokenFactory usernamePasswordTokenFactory() {
        return new UsernamePasswordTokenFactory();
    }

    private class AuthClassIgnoreCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            if (authClassNames == null || authClassNames.isEmpty()) {
                LOGGER.info("not found any auth class");
                return false;
            }
            return true;
        }
    }

    @Configuration
    @ConditionalOnMissingBean(FilterChainFilter.class)
    @Order
    @Conditional(AuthClassIgnoreCondition.class)
    class CoffeeAuthBuilders {
        private ObjectPostProcessor<Object> objectObjectPostProcessor;
        private UrlPathHelper urlPathHelper;
        private HttpSecurityManager securityManager;

        CoffeeAuthBuilders(ObjectPostProcessor<Object> objectObjectPostProcessor) {
            this.objectObjectPostProcessor = objectObjectPostProcessor;
        }

        @Bean
        public FilterChainFilter postProcessBeanDefinitionRegistry() {
            List<FilterChainManager> managers = new ArrayList<>();
            for (String className : authClassNames) {
                Class<?> clazz;
                try {
                    clazz = CoffeeAuthAutoConfiguration.class.getClassLoader().loadClass(className);
                } catch (ClassNotFoundException ignore) {
                    //not reachable
                    continue;
                }
                String simpleClassName = clazz.getSimpleName();
                String prefix = simpleClassName.substring(0, 1).toLowerCase()
                        + simpleClassName.substring(1);
                AuthEntity authEntity = AnnotatedElementUtils
                        .findMergedAnnotation(clazz, AuthEntity.class);
                boolean caseSensitive = authEntity.caseSensitive();
                String[] methods = authEntity.method();
                String[] roles = authEntity.roles();
                String[] baseUrls = authEntity.baseUrl();
                String url = authEntity.url();
                if (url.length() == 0) {
                    url = "/" + simpleClassName + "/login";
                }
                if (!url.startsWith("/")) {
                    url = "/" + url;
                }
                String finalUrl = url;
                OrRequestMatcher requestMatcher = new OrRequestMatcher(
                        Stream.of(methods).map(method ->
                                new AntPathRequestMatcher(finalUrl, method, caseSensitive, urlPathHelper))
                                .collect(Collectors.toList()));
                List<Filter> filters = new ArrayList<>(3);
                //认证上下文持久化过滤器
                AuthContextPersistenceFilter beforeFilter = objectObjectPostProcessor.postProcess(new AuthContextPersistenceFilter());
                filters.add(beforeFilter);
                //查询所有可用的tokenFactory，帮助填充到认证过滤器中
                Map<String, AuthenticationTokenFactory> factoryBeanMap = BeanFactoryUtils
                        .beansOfTypeIncludingAncestors((ListableBeanFactory) beanFactory,
                                AuthenticationTokenFactory.class);

                List<AuthenticationTokenFactory> factories = new ArrayList<>(factoryBeanMap.values());
                factories.sort(AnnotationAwareOrderComparator.INSTANCE);
                //认证过滤器
                ConfigurableAuthenticationFilter authenticationFilter = objectObjectPostProcessor
                        .postProcess(new ConfigurableAuthenticationFilter(requestMatcher, factories));

                Map<String, AuthenticationProvider> providerBeanMap = BeanFactoryUtils.beansOfTypeIncludingAncestors((ListableBeanFactory) beanFactory, AuthenticationProvider.class);
                Collection<AuthenticationProvider> values = providerBeanMap.values();
                List<AuthenticationProvider> providers = new ArrayList<>(values);
                providers.sort(AnnotationAwareOrderComparator.INSTANCE);
                ProviderManager providerManager = objectObjectPostProcessor.postProcess(new ProviderManager(providers));
                authenticationFilter.setAuthenticationManager(providerManager);
                filters.add(authenticationFilter);

                List<RequestMatcher> baseUrlMatchers = Stream.of(baseUrls).map(baseUrl -> {
                    if (!baseUrl.startsWith("/")) {
                        baseUrl = "/" + baseUrl;
                    }
                    return new AntPathRequestMatcher(baseUrl);
                }).collect(Collectors.toList());
                OrRequestMatcher baseRequestMatcher = new OrRequestMatcher(baseUrlMatchers);
                UrlBasedFilterChainManager urlBasedFilterChainManager = objectObjectPostProcessor.postProcess(new UrlBasedFilterChainManager(baseRequestMatcher, filters));
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
        public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
            this.urlPathHelper = urlPathHelper;
        }

        @Autowired(required = false)
        public void setSecurityManager(HttpSecurityManager securityManager) {
            this.securityManager = securityManager;
        }
    }

    private static class AnnotationAwareOrderComparator extends OrderComparator {
        private static final AnnotationAwareOrderComparator INSTANCE = new AnnotationAwareOrderComparator();

        @Override
        protected int getOrder(Object obj) {
            return lookupOrder(obj);
        }

        private static int lookupOrder(Object obj) {
            if (obj instanceof Ordered) {
                return ((Ordered) obj).getOrder();
            }
            if (obj != null) {
                Class<?> clazz = (obj instanceof Class ? (Class<?>) obj : obj.getClass());
                Order order = AnnotationUtils.findAnnotation(clazz, Order.class);
                if (order != null) {
                    return order.value();
                }
            }
            return Ordered.LOWEST_PRECEDENCE;
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
