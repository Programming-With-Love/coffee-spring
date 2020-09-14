package site.zido.coffee.mvc.exceptions;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import site.zido.coffee.mvc.rest.GlobalExceptionAdvice;
import site.zido.coffee.mvc.rest.HttpResponseBodyConfiguration;
import site.zido.coffee.mvc.rest.HttpResponseBodyFactory;

@Configuration
@Import(HttpResponseBodyConfiguration.class)
public class GlobalExceptionConfiguration {
    @Bean
    public GlobalExceptionAdvice advice(HttpResponseBodyFactory factory) {
        return new GlobalExceptionAdvice(factory);
    }
}
