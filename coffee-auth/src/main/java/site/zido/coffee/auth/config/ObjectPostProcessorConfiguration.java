package site.zido.coffee.auth.config;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author zido
 */
@Configuration
public class ObjectPostProcessorConfiguration {
    @Bean
    public ObjectPostProcessor<Object> objectObjectPostProcessor(AutowireCapableBeanFactory factory) {
        return new AutowireBeanFactoryObjectPostProcessor(factory);
    }
}
