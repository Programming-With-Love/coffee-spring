package site.zido.coffee.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.filter.CompositeFilter;
import site.zido.coffee.auth.config.ObjectPostProcessor;
import site.zido.coffee.auth.user.annotations.AuthEntity;
import site.zido.coffee.auth.web.FilterChainFilter;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.servlet.FilterChain;
import java.util.List;

/**
 * 认证相关自动配置
 *
 * @author zido
 */
@Configuration
public class CoffeeAuthAutoConfiguration implements ResourceLoaderAware,
        EnvironmentAware, BeanDefinitionRegistryPostProcessor, BeanFactoryAware {
    private static Logger LOGGER = LoggerFactory.getLogger(CoffeeAuthAutoConfiguration.class);
    private Environment environment;
    private ResourceLoader resourceLoader;
    private List<String> basePackages;
    private BeanFactory beanFactory;

    @Bean
    public List<FilterChainFilter> getGlobalFilters(ObjectPostProcessor<Object> objectPostProcessor) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.setEnvironment(environment);
        provider.setResourceLoader(resourceLoader);
        provider.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
        provider.addIncludeFilter(new AnnotationTypeFilter(MappedSuperclass.class));
        provider.addIncludeFilter(new AnnotationTypeFilter(AuthEntity.class));
        for (String basePackage : basePackages) {
            for (BeanDefinition definition : provider.findCandidateComponents(basePackage)) {
                String className = definition.getBeanClassName();
                try {
                    Class<?> clazz = CoffeeAuthAutoConfiguration.class.getClassLoader().loadClass(className);
                    FilterChainFilter filterChainFilter = objectPostProcessor.postProcess(new FilterChainFilter());
                } catch (ClassNotFoundException e) {
                    //ignore
                    LOGGER.warn(className + " can't load");
                }
            }
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        EntityScanPackages packages = EntityScanPackages.get(beanFactory);
        this.basePackages = packages.getPackageNames();
        this.beanFactory = beanFactory;
    }

}
