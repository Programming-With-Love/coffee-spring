package site.zido.coffee.extra.limiter;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import site.zido.coffee.core.utils.SystemClock;

import java.util.concurrent.TimeUnit;

/**
 * 基于redis的频率限制器
 * <p>
 * 使用场景：手机号发送短信验证码一分钟不能超过一次(建议时间设置比实际情况略小
 *
 * @author zido
 */
public class RedisFrequencyLimiter implements FrequencyLimiter {

    private static final String PRE = "coffee:limiter:";
    private final String prefix;
    private final RedisTemplate<String, Long> template;

    public RedisFrequencyLimiter(String prefix, RedisTemplate<String, Long> template) {
        this.prefix = prefix;
        this.template = template;
    }

    public RedisFrequencyLimiter(RedisTemplate<String, Long> template) {
        this(PRE, template);
    }

    @Override
    public long tryGet(String key, long timeout) {
        Assert.isTrue(timeout > 1, "超时时间设定以秒为单位，并且需要大于一秒");
        Assert.isTrue(timeout <= Integer.MAX_VALUE, "超时时间需要小于等于" + Integer.MAX_VALUE);
        long now = SystemClock.now();
        now = now / 1000;
        String prefixedKey = prefix + key;
        Long expire = template.getExpire(prefixedKey, TimeUnit.MILLISECONDS);
        //redis集群可能无法准确过期，用ttl判断更准确
        if (expire != null) {
            //如果值永久有效将永远无法有效获取
            if (expire == -1) {
                throw new IllegalStateException(String.format("键[%s]永久有效，需要排查", prefixedKey));
            }
            if (expire > 0) {
                return expire;
            }
        }
        createTag(key, now, timeout);
        return 0;
    }

    private void createTag(String key, long date, long timeout) {
        template.opsForValue().set(prefix + key, date, timeout);
    }
}
