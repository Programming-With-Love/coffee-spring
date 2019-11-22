package org.springframework.security.config.annotation.web.configuration;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.crypto.RsaKeyConversionServicePostProcessor;
import org.springframework.security.context.DelegatingApplicationListener;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import site.zido.coffee.security.RestSecurityConfigurationAdapter;

import javax.servlet.Filter;
import java.util.List;
import java.util.Map;

/**
 * @author zido
 */
@Configuration(proxyBeanMethods = false)
public class RestSecurityConfiguration implements ImportAware, BeanClassLoaderAware {
    private WebSecurity webSecurity;

    private Boolean debugEnabled;

    private List<SecurityConfigurer<Filter, WebSecurity>> webSecurityConfigurers;

    private ClassLoader beanClassLoader;

    @Autowired(required = false)
    private ObjectPostProcessor<Object> objectObjectPostProcessor;

    @Bean
    public static DelegatingApplicationListener delegatingApplicationListener() {
        return new DelegatingApplicationListener();
    }

    @Bean
    @DependsOn(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME)
    public SecurityExpressionHandler<FilterInvocation> webSecurityExpressionHandler() {
        return webSecurity.getExpressionHandler();
    }

    /**
     * Creates the Spring Security Filter Chain
     *
     * @return the {@link Filter} that represents the security filter chain
     * @throws Exception
     */
    @Bean(name = AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME)
    public Filter springSecurityFilterChain() throws Exception {
        boolean hasConfigurers = webSecurityConfigurers != null
                && !webSecurityConfigurers.isEmpty();
        if (!hasConfigurers) {
            RestSecurityConfigurationAdapter adapter = objectObjectPostProcessor
                    .postProcess(new RestSecurityConfigurationAdapter() {
                    });
            webSecurity.apply(adapter);
        }
        return webSecurity.build();
    }

    /**
     * Creates the {@link WebInvocationPrivilegeEvaluator} that is necessary for the JSP
     * tag support.
     *
     * @return the {@link WebInvocationPrivilegeEvaluator}
     */
    @Bean
    @DependsOn(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME)
    public WebInvocationPrivilegeEvaluator privilegeEvaluator() {
        return webSecurity.getPrivilegeEvaluator();
    }

    /**
     * Sets the {@code <SecurityConfigurer<FilterChainProxy, WebSecurityBuilder>}
     * instances used to create the web configuration.
     *
     * @param objectPostProcessor    the {@link ObjectPostProcessor} used to create a
     *                               {@link WebSecurity} instance
     * @param webSecurityConfigurers the
     *                               {@code <SecurityConfigurer<FilterChainProxy, WebSecurityBuilder>} instances used to
     *                               create the web configuration
     * @throws Exception
     */
    @Autowired(required = false)
    public void setFilterChainProxySecurityConfigurer(
            ObjectPostProcessor<Object> objectPostProcessor,
            @Value("#{@autowiredWebSecurityConfigurersIgnoreParents.getWebSecurityConfigurers()}") List<SecurityConfigurer<Filter, WebSecurity>> webSecurityConfigurers)
            throws Exception {
        webSecurity = objectPostProcessor
                .postProcess(new WebSecurity(objectPostProcessor));
        if (debugEnabled != null) {
            webSecurity.debug(debugEnabled);
        }

        webSecurityConfigurers.sort(RestSecurityConfiguration.AnnotationAwareOrderComparator.INSTANCE);

        Integer previousOrder = null;
        Object previousConfig = null;
        for (SecurityConfigurer<Filter, WebSecurity> config : webSecurityConfigurers) {
            Integer order = RestSecurityConfiguration.AnnotationAwareOrderComparator.lookupOrder(config);
            if (previousOrder != null && previousOrder.equals(order)) {
                throw new IllegalStateException(
                        "@Order on WebSecurityConfigurers must be unique. Order of "
                                + order + " was already used on " + previousConfig + ", so it cannot be used on "
                                + config + " too.");
            }
            previousOrder = order;
            previousConfig = config;
        }
        for (SecurityConfigurer<Filter, WebSecurity> webSecurityConfigurer : webSecurityConfigurers) {
            webSecurity.apply(webSecurityConfigurer);
        }
        this.webSecurityConfigurers = webSecurityConfigurers;
    }

    @Bean
    public static BeanFactoryPostProcessor conversionServicePostProcessor() {
        return new RsaKeyConversionServicePostProcessor();
    }

    @Bean
    public static AutowiredWebSecurityConfigurersIgnoreParents autowiredWebSecurityConfigurersIgnoreParents(
            ConfigurableListableBeanFactory beanFactory) {
        return new AutowiredWebSecurityConfigurersIgnoreParents(beanFactory);
    }

    /**
     * A custom verision of the Spring provided AnnotationAwareOrderComparator that uses
     * {@link AnnotationUtils#findAnnotation(Class, Class)} to look on super class
     * instances for the {@link Order} annotation.
     *
     * @author Rob Winch
     * @since 3.2
     */
    private static class AnnotationAwareOrderComparator extends OrderComparator {
        private static final RestSecurityConfiguration.AnnotationAwareOrderComparator INSTANCE = new RestSecurityConfiguration.AnnotationAwareOrderComparator();

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

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.context.annotation.ImportAware#setImportMetadata(org.
     * springframework.core.type.AnnotationMetadata)
     */
    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        Map<String, Object> enableWebSecurityAttrMap = importMetadata
                .getAnnotationAttributes(EnableRestSecurity.class.getName());
        AnnotationAttributes enableWebSecurityAttrs = AnnotationAttributes
                .fromMap(enableWebSecurityAttrMap);
        debugEnabled = enableWebSecurityAttrs.getBoolean("debug");
        if (webSecurity != null) {
            webSecurity.debug(debugEnabled);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.beans.factory.BeanClassLoaderAware#setBeanClassLoader(java.
     * lang.ClassLoader)
     */
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }
}
