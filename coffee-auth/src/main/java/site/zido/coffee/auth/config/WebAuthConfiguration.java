package site.zido.coffee.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;

import javax.servlet.Filter;
import java.util.List;

/**
 * @author zido
 */
@Configuration
public class WebAuthConfiguration {
    private FilterChainFilterBuilder globalBuilder;
    private List<AuthConfigurer<Filter, FilterChainFilterBuilder>> globalConfigurers;

    @Autowired(required = false)
    private ObjectPostProcessor<Object> objectPostProcessor;

    @Bean
    public static AutowiredWebAuthConfigurersScanner globalWebAuthScanner(
            ConfigurableListableBeanFactory beanFactory) {
        return new AutowiredWebAuthConfigurersScanner(beanFactory);
    }

    @Bean
    public Filter coffeeAuthFilterChainFilter() throws Exception {
        boolean hasConfigurers = globalConfigurers != null && !globalConfigurers.isEmpty();
        if (!hasConfigurers) {
            WebAuthConfigurerAdapter adapter = objectPostProcessor.postProcess(new WebAuthConfigurerAdapter() {
            });
            globalBuilder.apply(adapter);
        }
        return globalBuilder.build();
    }

    @Autowired(required = false)
    public void setFilterChainFilterConfigurer(
            ObjectPostProcessor<Object> objectPostProcessor,
            @Value("#{globalWebAuthScanner.getConfigurers()}") List<AuthConfigurer<Filter, FilterChainFilterBuilder>> globalConfigurers) throws Exception {
        globalBuilder = objectPostProcessor
                .postProcess(new FilterChainFilterBuilder(objectPostProcessor));
        globalConfigurers.sort(AnnotationAwareOrderComparator.INSTANCE);
        for (AuthConfigurer<Filter, FilterChainFilterBuilder> globalConfigurer : globalConfigurers) {
            globalBuilder.apply(globalConfigurer);
        }
        this.globalConfigurers = globalConfigurers;
    }

}
