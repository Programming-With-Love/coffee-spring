package site.zido.coffee.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import site.zido.coffee.auth.config.*;
import site.zido.coffee.auth.context.AuthContextPersistenceFilter;
import site.zido.coffee.auth.context.HttpSessionUserContextRepository;
import site.zido.coffee.auth.context.UserContextRepository;
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

/**
 * 认证相关自动配置
 *
 * @author zido
 */
@Configuration
@AutoConfigureAfter(JpaRepositoriesAutoConfiguration.class)
@Import({AuthCommonConfiguration.class, ObjectPostProcessorConfiguration.class})
public class CoffeeAuthAutoConfiguration {
    private static Logger LOGGER = LoggerFactory.getLogger(CoffeeAuthAutoConfiguration.class);
    private List<String> authClassNames;

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
    class CoffeeAuthBuilders implements ApplicationContextAware {
        private ObjectPostProcessor<Object> objectObjectPostProcessor;
        private HttpSecurityManager securityManager;
        private ApplicationContext context;

        CoffeeAuthBuilders(ObjectPostProcessor<Object> objectObjectPostProcessor) {
            this.objectObjectPostProcessor = objectObjectPostProcessor;
        }

        @Bean
        public FilterChainFilter postProcessBeanDefinitionRegistry() {
            List<FilterChainManager> managers = new ArrayList<>();
            Map<String, AuthenticationFilterFactory> authenticationFilterFactoryMap = BeanFactoryUtils.beansOfTypeIncludingAncestors(context,
                    AuthenticationFilterFactory.class);
            for (String className : authClassNames) {
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
                    filters.add(factory.createFilter(clazz, objectObjectPostProcessor));
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
    }

}
