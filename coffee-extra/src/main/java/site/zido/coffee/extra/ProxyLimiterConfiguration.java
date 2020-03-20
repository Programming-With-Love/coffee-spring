package site.zido.coffee.extra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;
import site.zido.coffee.extra.limiter.*;

import java.nio.charset.StandardCharsets;

/**
 * @author zido
 */
@ConditionalOnProperty(prefix = "site.zido.limiter", value = "enable", matchIfMissing = true)
@Configuration
public class ProxyLimiterConfiguration {
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public BeanFactoryLimiterOperationSourceAdvisor limiterAdvisor(LimiterInterceptor interceptor) {
        BeanFactoryLimiterOperationSourceAdvisor advisor = new BeanFactoryLimiterOperationSourceAdvisor();
        advisor.setAdvice(interceptor);
        advisor.setLimiterOperationSource(limiterOperationSource());
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LimiterOperationSource limiterOperationSource() {
        return new AnnotationLimiterOperationSource();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LimiterInterceptor interceptor(FrequencyLimiter limiter) {
        LimiterInterceptor interceptor = new LimiterInterceptor();
        interceptor.setLimiterOperationSource(limiterOperationSource());
        interceptor.setLimiter(limiter);
        return interceptor;
    }

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
            private Logger logger = LoggerFactory.getLogger("limiterValueSerializer");

            @Override
            public byte[] serialize(Long value) throws SerializationException {
                return (value + "").getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Long deserialize(byte[] bytes) throws SerializationException {
                try {
                    return Long.parseLong(new String(bytes, StandardCharsets.UTF_8));
                } catch (Throwable t) {
                    logger.error("deserialize error", t);
                    return null;
                }
            }
        });
        return template;
    }

    @Bean
    public RedisFrequencyLimiter limiter(@Autowired LimiterProperties properties,
                                         @Autowired @Qualifier(value = "limiterTemplate") RedisTemplate<String, Long> template) {
        if (StringUtils.hasLength(properties.getPrefix())) {
            return new RedisFrequencyLimiter(properties.getPrefix(), template);
        }
        return new RedisFrequencyLimiter(template);
    }

    @Bean
    @ConfigurationProperties(prefix = "coffee.limiter")
    public LimiterProperties createProperties() {
        return new LimiterProperties();
    }

    class LimiterProperties {
        private String prefix;

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }
    }
}
