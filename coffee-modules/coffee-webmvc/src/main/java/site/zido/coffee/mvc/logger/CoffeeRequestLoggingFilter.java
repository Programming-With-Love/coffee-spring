package site.zido.coffee.mvc.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

public class CoffeeRequestLoggingFilter extends AbstractRequestLoggingFilter {
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
}
