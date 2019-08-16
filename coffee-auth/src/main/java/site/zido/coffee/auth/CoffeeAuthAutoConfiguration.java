package site.zido.coffee.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.web.util.UrlPathHelper;
import site.zido.coffee.auth.authentication.AuthenticationTokenFactory;
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
import site.zido.coffee.auth.web.UrlBasedFilterChainManager;
import site.zido.coffee.auth.web.authentication.ConfigurableAuthenticationFilter;
import site.zido.coffee.auth.web.utils.matcher.AntPathRequestMatcher;
import site.zido.coffee.auth.web.utils.matcher.OrRequestMatcher;
import site.zido.coffee.auth.web.utils.matcher.RequestMatcher;

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
@Import(AuthCommonConfiguration.class)
public class CoffeeAuthAutoConfiguration implements ResourceLoaderAware,
        EnvironmentAware, BeanFactoryAware {
    private static Logger LOGGER = LoggerFactory.getLogger(CoffeeAuthAutoConfiguration.class);
    private Environment environment;
    private ResourceLoader resourceLoader;
    private List<String> authClassNames;
    private BeanFactory beanFactory;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

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

    @Configuration
    @ConditionalOnMissingBean(FilterChainFilter.class)
    @Order
    class AuthGlobalRegisterProcessor implements BeanDefinitionRegistryPostProcessor {
        private ObjectPostProcessor<Object> objectObjectPostProcessor;
        private UrlPathHelper urlPathHelper;

        AuthGlobalRegisterProcessor(ObjectPostProcessor<Object> objectObjectPostProcessor) {
            this.objectObjectPostProcessor = objectObjectPostProcessor;
        }

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            if (authClassNames == null || authClassNames.isEmpty()) {
                LOGGER.info("not found any auth class");
                return;
            }
            List<FilterChainManager> managers = new ArrayList<>();
            for (String className : authClassNames) {
                Class<?> clazz = null;
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
                List<String> filterRefs = new ArrayList<>(3);
                //认证上下文持久化过滤器
                AbstractBeanDefinition beforeFilterDefinition = BeanDefinitionBuilder
                        .genericBeanDefinition(AuthContextPersistenceFilter.class)
                        .getBeanDefinition();
                String beforeRef = prefix + "BeforeFilter";
                registry.registerBeanDefinition(beforeRef,
                        beforeFilterDefinition);
                filterRefs.add(beforeRef);

                //查询所有可用的tokenFactory，帮助填充到认证过滤器中
                Map<String, AuthenticationTokenFactory> factories = BeanFactoryUtils
                        .beansOfTypeIncludingAncestors((ListableBeanFactory) beanFactory,
                                AuthenticationTokenFactory.class);

                //认证过滤器
                AbstractBeanDefinition authenticationFilter = BeanDefinitionBuilder
                        .genericBeanDefinition(ConfigurableAuthenticationFilter.class)
                        .addConstructorArgValue(requestMatcher)
                        .addConstructorArgValue(factories.values())
                        .getBeanDefinition();
                String authRef = prefix + "AuthenticationFilter";
                registry.registerBeanDefinition(authRef,
                        authenticationFilter);
                filterRefs.add(authRef);

                List<RequestMatcher> baseUrlMatchers = Stream.of(baseUrls).map(baseUrl -> {
                    if (!baseUrl.startsWith("/")) {
                        baseUrl = "/" + baseUrl;
                    }
                    return new AntPathRequestMatcher(baseUrl);
                }).collect(Collectors.toList());
                OrRequestMatcher baseRequestMatcher = new OrRequestMatcher(baseUrlMatchers);
                BeanDefinitionBuilder builder = BeanDefinitionBuilder
                        .genericBeanDefinition(UrlBasedFilterChainManager.class)
                        .addConstructorArgValue(baseRequestMatcher);
                for (String ref : filterRefs) {
                    builder.addConstructorArgReference(ref);
                }
                AbstractBeanDefinition filterChainManagerDefinition = builder.getBeanDefinition();
                registry.registerBeanDefinition(prefix + "FilterChainManager",
                        filterChainManagerDefinition);
            }
            AbstractBeanDefinition filterChainDefinition = BeanDefinitionBuilder
                    .genericBeanDefinition(FilterChainFilter.class)
                    .getBeanDefinition();
            registry.registerBeanDefinition("filterChainFilter", filterChainDefinition);
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        }

        @Autowired(required = false)
        public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
            this.urlPathHelper = urlPathHelper;
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
