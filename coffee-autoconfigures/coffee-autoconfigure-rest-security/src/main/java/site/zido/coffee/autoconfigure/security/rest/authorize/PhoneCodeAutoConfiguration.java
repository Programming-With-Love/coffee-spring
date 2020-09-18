package site.zido.coffee.autoconfigure.security.rest.authorize;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import site.zido.coffee.autoconfigure.security.rest.CoffeeSecurityProperties;
import site.zido.coffee.security.authentication.phone.MemoryPhoneCodeCache;
import site.zido.coffee.security.authentication.phone.PhoneCodeCache;

@Configuration
@EnableConfigurationProperties({CoffeeSecurityProperties.class})
@AutoConfigureAfter(RedisPhoneCodeConfiguration.class)
public class PhoneCodeAutoConfiguration {
    @ConditionalOnMissingBean(PhoneCodeCache.class)
    @Bean
    public PhoneCodeCache getCache(CoffeeSecurityProperties properties) {
        return new MemoryPhoneCodeCache() {
            @Override
            public void put(String phone, String code) {
                super.put(getKey(phone), code);
            }

            @Override
            public String getCode(String phone) {
                return super.getCode(getKey(phone));
            }

            private String getKey(String key) {
                return properties.getPhoneCode().getKeyPrefix() + key;
            }

            @Override
            public long getTimeout() {
                return properties.getPhoneCode().getTimeout() == null
                        ? super.getTimeout()
                        : properties.getPhoneCode().getTimeout();
            }
        };
    }
}
