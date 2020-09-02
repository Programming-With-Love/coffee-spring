package site.zido.coffee.autoconfigure.extra.limiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;
import site.zido.coffee.extra.limiter.EnableLimiter;
import site.zido.coffee.extra.limiter.FrequencyLimiter;
import site.zido.coffee.extra.limiter.MemoryFrequencyLimiter;
import site.zido.coffee.extra.limiter.RedisFrequencyLimiter;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableConfigurationProperties(LimiterProperties.class)
@EnableLimiter
@AutoConfigureAfter(LimiterRedisConfiguration.class)
public class LimiterAutoConfiguration {
    @Bean(name = "limiterExceptionHandler")
    @ConditionalOnMissingBean(name = "limiterExceptionHandler")
    public LimiterExceptionAdvice advice() {
        return new LimiterExceptionAdvice();
    }

    @Bean
    @ConditionalOnMissingBean(name = "limiterTemplate", value = FrequencyLimiter.class)
    public FrequencyLimiter limiter(@Autowired LimiterProperties properties) {
        if (StringUtils.hasLength(properties.getPrefix())) {
            return new MemoryFrequencyLimiter(properties.getPrefix());
        }
        return new MemoryFrequencyLimiter();
    }
}
