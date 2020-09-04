package site.zido.coffee.autoconfigure.security.rest.authorize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import site.zido.coffee.autoconfigure.security.rest.CoffeeSecurityProperties;
import site.zido.coffee.security.authentication.phone.PhoneCodeCache;
import site.zido.coffee.security.authentication.phone.SpringRedisPhoneCodeCache;

import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnBean(RedisConnectionFactory.class)
@EnableConfigurationProperties(CoffeeSecurityProperties.AuthorizationPhoneCodeProperties.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisPhoneCodeConfiguration {
    public static final String TEMPLATE_BEAN_NAME = "phoneCodeCacheTemplate";

    @Bean
    @ConditionalOnMissingBean(PhoneCodeCache.class)
    public PhoneCodeCache phoneCodeCache(@Autowired CoffeeSecurityProperties.AuthorizationPhoneCodeProperties properties,
                                         @Autowired @Qualifier(TEMPLATE_BEAN_NAME) StringRedisTemplate template) {
        SpringRedisPhoneCodeCache cache = new SpringRedisPhoneCodeCache();
        cache.setKeyPrefix(properties.getKeyPrefix());
        cache.setTimeout(properties.getTimeout(), TimeUnit.SECONDS);
        cache.setTemplate(template);
        return cache;
    }

    @Bean(value = TEMPLATE_BEAN_NAME)
    @ConditionalOnMissingBean(name = TEMPLATE_BEAN_NAME)
    public StringRedisTemplate createTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
}
