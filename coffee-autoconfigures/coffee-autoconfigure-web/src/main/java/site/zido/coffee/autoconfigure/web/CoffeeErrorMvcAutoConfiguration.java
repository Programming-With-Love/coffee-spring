package site.zido.coffee.autoconfigure.web;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import site.zido.coffee.autoconfigure.web.basic.CoffeeErrorAttributes;
import site.zido.coffee.mvc.rest.HttpResponseBodyConfiguration;
import site.zido.coffee.mvc.rest.HttpResponseBodyFactory;

@Configuration
@EnableConfigurationProperties({ServerProperties.class})
@Import(HttpResponseBodyConfiguration.class)
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
@ConditionalOnProperty(value = "spring.coffee.web.global-exception", matchIfMissing = true, havingValue = "true")
class CoffeeErrorMvcAutoConfiguration {
    private final ServerProperties serverProperties;

    public CoffeeErrorMvcAutoConfiguration(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
    @Bean
    public CoffeeErrorAttributes errorAttributes(HttpResponseBodyFactory factory) {
        return new CoffeeErrorAttributes(this.serverProperties.getError().isIncludeException(), factory);
    }
}
