package site.zido.coffee.autoconfigure.extra.limiter;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import site.zido.coffee.mvc.rest.HttpResponseBodyFactory;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnBean(HttpResponseBodyFactory.class)
@AutoConfigureAfter(name = "site.zido.coffee.autoconfigure.web.GlobalResultEnablerConfiguration")
public class LimiterExceptionAutoHandlerConfiguration {
    @Bean(name = "limiterExceptionHandler")
    @ConditionalOnMissingBean(name = "limiterExceptionHandler")
    public LimiterExceptionAdvice advice(HttpResponseBodyFactory factory) {
        return new LimiterExceptionAdvice(factory);
    }
}
