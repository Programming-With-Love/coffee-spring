package site.zido.coffee.auth.web.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import site.zido.coffee.auth.web.FilterChainProxy;

import javax.servlet.Filter;


@Configuration
public class WebAuthConfiguration {

    @Bean
    public Filter getFilter() throws Exception {
        return new FilterChainProxy();
    }

}
