package site.zido.coffee.mvc.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import site.zido.coffee.mvc.rest.DefaultHttpResponseBodyFactory;
import site.zido.coffee.mvc.rest.GlobalExceptionAdvice;
import site.zido.coffee.mvc.rest.HttpResponseBodyFactory;

@Configuration
public class MvcCommonConfiguration {
    @Bean
    public HttpResponseBodyFactory bodyFactory() {
        return new DefaultHttpResponseBodyFactory();
    }

    @Bean
    public GlobalExceptionAdvice advice() {
        return new GlobalExceptionAdvice(bodyFactory());
    }
}
