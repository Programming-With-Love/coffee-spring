package site.zido.coffee.autoconfigure.extra.limiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import site.zido.coffee.extra.limiter.EnableLimiter;
import site.zido.coffee.extra.limiter.FrequencyLimiter;
import site.zido.coffee.extra.limiter.MemoryFrequencyLimiter;

@Configuration
@EnableConfigurationProperties(LimiterProperties.class)
@EnableLimiter
@AutoConfigureAfter(LimiterRedisConfiguration.class)
public class LimiterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "limiterTemplate", value = FrequencyLimiter.class)
    public FrequencyLimiter limiter(@Autowired LimiterProperties properties) {
        if (StringUtils.hasLength(properties.getPrefix())) {
            return new MemoryFrequencyLimiter(properties.getPrefix());
        }
        return new MemoryFrequencyLimiter();
    }
}
