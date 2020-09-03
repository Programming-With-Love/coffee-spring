package site.zido.coffee.extra.limiter;

import site.zido.coffee.core.utils.maps.expire.ExpireMap;

/**
 * 基于内存的频率限制
 *
 * @author zido
 */
public class MemoryFrequencyLimiter implements FrequencyLimiter {
    private static final Object PRESENT = new Object();
    private final ExpireMap<String, Object> expireMap = new ExpireMap<>();
    private String prefix = "";

    public MemoryFrequencyLimiter() {

    }

    public MemoryFrequencyLimiter(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public long tryGet(String key, long timeout) {
        key = getKey(key);
        long crt = timeout * 1000;
        return expireMap.setNx(key, PRESENT, crt);
    }

    protected String getKey(String key) {
        return prefix + key;
    }
}
