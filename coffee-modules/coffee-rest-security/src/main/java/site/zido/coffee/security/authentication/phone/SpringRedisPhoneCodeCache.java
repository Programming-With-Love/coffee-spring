package site.zido.coffee.security.authentication.phone;

import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 使用spring redis template实现的手机号验证码缓存
 *
 * @author zido
 */
public class SpringRedisPhoneCodeCache implements PhoneCodeCache, InitializingBean {
    private StringRedisTemplate template;
    private String keyPrefix;
    private long timeout;
    private TimeUnit unit;

    public SpringRedisPhoneCodeCache() {
        this.setKeyPrefix("coffee:phone:");
        this.setTimeout(60, TimeUnit.SECONDS);
    }

    @Override
    public void put(String phone, String code) {
        template.opsForValue().set(getKey(phone), code, timeout, unit);
    }

    @Override
    public String getCode(String phone) {
        return template.opsForValue().get(getKey(phone));
    }

    @Autowired
    public void setTemplate(StringRedisTemplate template) {
        this.template = template;
    }

    protected String getKey(String phone) {
        return keyPrefix + phone;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public void setTimeout(long timeout, TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(template, "redis template cannot be null");
    }
}
