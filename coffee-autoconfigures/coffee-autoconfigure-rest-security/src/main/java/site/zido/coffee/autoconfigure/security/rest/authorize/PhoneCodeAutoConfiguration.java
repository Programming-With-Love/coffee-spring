package site.zido.coffee.autoconfigure.security.rest.authorize;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
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
                super.put(phone, code);
            }

            @Override
            public String getCode(String phone) {
                if (StringUtils.hasText(properties.getPhoneCode().getKeyPrefix())) {
                    phone = properties.getPhoneCode().getKeyPrefix() + phone;
                }
                return super.getCode(phone);
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
