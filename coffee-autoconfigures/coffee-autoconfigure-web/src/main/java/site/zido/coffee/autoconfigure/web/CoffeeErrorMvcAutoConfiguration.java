package site.zido.coffee.autoconfigure.web;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import site.zido.coffee.autoconfigure.web.basic.CoffeeErrorAttributes;
import site.zido.coffee.mvc.rest.HttpResponseBodyConfiguration;
import site.zido.coffee.mvc.rest.HttpResponseBodyFactory;
import site.zido.coffee.mvc.rest.OriginalResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Bean
    @ConditionalOnMissingBean(value = ErrorController.class, search = SearchStrategy.CURRENT)
    public BasicErrorController basicErrorController(ErrorAttributes errorAttributes,
                                                     ObjectProvider<ErrorViewResolver> errorViewResolvers) {
        return new BasicErrorController(errorAttributes, this.serverProperties.getError(),
                errorViewResolvers.orderedStream().collect(Collectors.toList())) {
            @RequestMapping
            @OriginalResponse
            public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
                HttpStatus status = getStatus(request);
                if (status == HttpStatus.NO_CONTENT) {
                    return new ResponseEntity<>(status);
                }
                Map<String, Object> body = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL));
                return new ResponseEntity<>(body, status);
            }
        };
    }
}
