package site.zido.coffee.mvc.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import site.zido.coffee.mvc.rest.DefaultHttpResponseBodyFactory;
import site.zido.coffee.mvc.rest.HttpResponseBodyFactory;

@Configuration
public class HttpResponseBodyConfiguration {
    @Bean
    public HttpResponseBodyFactory bodyFactory() {
        return new DefaultHttpResponseBodyFactory();
    }
}
