package site.zido.coffee.auth;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import site.zido.coffee.CommonAutoConfiguration;
import site.zido.coffee.auth.config.FilterChainFilterFactoryBean;
import site.zido.coffee.auth.config.ObjectPostProcessor;
import site.zido.coffee.auth.config.ObjectPostProcessorConfiguration;
import site.zido.coffee.auth.context.AuthPrincipalArgumentResolver;

import java.util.List;

@Configuration
@AutoConfigureAfter({
        CommonAutoConfiguration.JsonAutoConfiguration.class
})
public class AuthGlobalConfiguration {
    @Configuration
    @Import(ObjectPostProcessorConfiguration.class)
    @AutoConfigureAfter(
            JpaRepositoriesAutoConfiguration.class)
    static class BuilderConfiguration {
        @Bean
        public FilterChainFilterFactoryBean coffeeAuthBuilders(ObjectPostProcessor<Object> opp) {
            return new FilterChainFilterFactoryBean(opp);
        }
    }

    @Configuration
    static class WebMvcAuthConfiguration extends WebMvcConfigurerAdapter {
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
            argumentResolvers.add(new AuthPrincipalArgumentResolver());
        }
    }
}
