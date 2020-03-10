package site.zido.demo.config;

import org.springframework.context.annotation.Configuration;
import site.zido.coffee.extra.limiter.EnableLimiter;

/**
 * 启动限流器注解支持
 *
 * @author zido
 */
@EnableLimiter
@Configuration
public class LimiterConfiguration {
}
