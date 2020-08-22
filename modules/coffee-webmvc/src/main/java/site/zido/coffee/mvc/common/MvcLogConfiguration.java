package site.zido.coffee.mvc.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * 请求日志配置
 */
@Configuration
public class MvcLogConfiguration {
    @Bean
    public AbstractRequestLoggingFilter filter() {
        AbstractRequestLoggingFilter filter = new AbstractRequestLoggingFilter() {
            final Logger logger = LoggerFactory.getLogger("RequestLog");

            @Override
            protected boolean shouldLog(HttpServletRequest request) {
                return logger.isInfoEnabled();
            }

            @Override
            protected void beforeRequest(HttpServletRequest httpServletRequest, String s) {
                logger.info(s);
            }

            @Override
            protected void afterRequest(HttpServletRequest httpServletRequest, String s) {
                logger.info(s);
            }
        };
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        return filter;
    }
}
