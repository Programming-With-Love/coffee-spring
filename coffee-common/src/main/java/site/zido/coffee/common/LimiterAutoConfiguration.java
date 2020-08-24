package site.zido.coffee.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;
import site.zido.coffee.extra.limiter.FrequencyLimiter;
import site.zido.coffee.extra.limiter.MemoryFrequencyLimiter;
import site.zido.coffee.extra.limiter.RedisFrequencyLimiter;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class LimiterAutoConfiguration {
    @Bean(name = "limiterExceptionHandler")
    @ConditionalOnMissingBean(name = "limiterExceptionHandler")
    public LimiterExceptionAdvice advice() {
        return new LimiterExceptionAdvice();
    }

    @Bean(name = "limiterTemplate")
    @ConditionalOnMissingBean(name = "limiterTemplate")
    @ConditionalOnBean(RedisConnectionFactory.class)
    public RedisTemplate<String, Long> template(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer(StandardCharsets.UTF_8));
        template.setValueSerializer(new RedisSerializer<Long>() {

            @Override
            public byte[] serialize(Long value) throws SerializationException {
                return ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(value).array();
            }

            @Override
            public Long deserialize(byte[] bytes) throws SerializationException {
                ByteBuffer buffer = ByteBuffer.allocate(8);
                buffer.put(bytes, 0, bytes.length);
                buffer.flip();
                return buffer.getLong();
            }
        });
        return template;
    }

    @Bean
    @ConditionalOnBean(name = "limiterTemplate")
    @ConditionalOnMissingBean(FrequencyLimiter.class)
    public FrequencyLimiter limiter(@Autowired LimiterProperties properties,
                                    @Autowired @Qualifier(value = "limiterTemplate") RedisTemplate<String, Long> template) {
        if (StringUtils.hasLength(properties.getPrefix())) {
            return new RedisFrequencyLimiter(properties.getPrefix(), template);
        }
        return new RedisFrequencyLimiter(template);
    }

    @Bean
    @ConditionalOnMissingBean(name = "limiterTemplate", value = FrequencyLimiter.class)
    public FrequencyLimiter limiter(@Autowired LimiterProperties properties) {
        if (StringUtils.hasLength(properties.getPrefix())) {
            return new MemoryFrequencyLimiter(properties.getPrefix());
        }
        return new MemoryFrequencyLimiter();
    }

    @Bean
    @ConfigurationProperties(prefix = "coffee.limiter")
    public LimiterProperties createProperties() {
        return new LimiterProperties();
    }

    static class LimiterProperties {
        private String prefix;

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }
    }
}
