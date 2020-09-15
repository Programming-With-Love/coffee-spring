package site.zido.coffee.mvc.logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

/**
 * 请求日志配置
 */
@Configuration
public class MvcLogConfiguration {
    @Bean
    public AbstractRequestLoggingFilter filter() {
        AbstractRequestLoggingFilter filter = new CoffeeRequestLoggingFilter();
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        return filter;
    }
}
