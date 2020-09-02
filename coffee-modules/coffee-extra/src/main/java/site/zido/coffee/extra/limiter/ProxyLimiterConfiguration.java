package site.zido.coffee.extra.limiter;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @author zido
 */
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ProxyLimiterConfiguration {
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public BeanFactoryLimiterOperationSourceAdvisor limiterAdvisor(LimiterInterceptor interceptor) {
        BeanFactoryLimiterOperationSourceAdvisor advisor = new BeanFactoryLimiterOperationSourceAdvisor();
        advisor.setAdvice(interceptor);
        advisor.setLimiterOperationSource(limiterOperationSource());
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LimiterOperationSource limiterOperationSource() {
        return new AnnotationLimiterOperationSource();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LimiterInterceptor interceptor(FrequencyLimiter limiter) {
        LimiterInterceptor interceptor = new LimiterInterceptor();
        interceptor.setLimiterOperationSource(limiterOperationSource());
        interceptor.setLimiter(limiter);
        return interceptor;
    }
}
