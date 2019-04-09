package site.zido.coffee.extra.limiter;

import site.zido.coffee.common.utils.SystemClock;
import org.springframework.data.redis.core.RedisTemplate;

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
    private String prefix;
    private RedisTemplate<String, Long> template;

    public RedisFrequencyLimiter(String prefix, RedisTemplate<String, Long> template) {
        this.prefix = prefix;
        this.template = template;
    }

    public RedisFrequencyLimiter(RedisTemplate<String, Long> template) {
        this(PRE, template);
    }

    @Override
    public LastItem tryGetForItem(String key, long timeout, TimeUnit unit) {
        long l = unit.toSeconds(timeout);
        if (l < 1) {
            throw new IllegalArgumentException("超时时间需要大于1S");
        }
        if (l >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("超时间需要小于" + Integer.MAX_VALUE);
        }
        long now = SystemClock.now();
        now = now / 1000;
        String prefixedKey = prefix + key;
        Long expire = template.getExpire(prefixedKey, TimeUnit.MILLISECONDS);
        //redis集群可能无法准确过期，用ttl判断更准确
        //如果值永久有效将永远无法有效获取
        if (expire == -1) {
            throw new IllegalStateException(String.format("键[%s]永久有效，需要排查", prefixedKey));
        }
        if (expire > 0) {
            return new LastItem(expire, TimeUnit.MILLISECONDS);
        }
        createTag(key, now, timeout, unit);
        return null;
    }

    private void createTag(String key, long date, long timeout, TimeUnit unit) {
        template.opsForValue().set(prefix + key, date, timeout, unit);
    }
}
