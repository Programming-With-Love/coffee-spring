package site.zido.coffee.extra.limiter;

import java.util.concurrent.TimeUnit;

/**
 * 当方法调用频率被限制时，抛出的异常
 *
 * @see LimiterErrorHandler
 * @see FrequencyLimiter
 * @author zido
 */
public class LimiterException extends RuntimeException {
    private static final long serialVersionUID = -8703559654766706392L;
    /**
     * 限制的key
     */
    private Object key;
    /**
     * 剩余时间
     */
    private long last;
    private TimeUnit lastUnit;
    /**
     * 下次需要的时间
     */
    private long requireTime;
    private TimeUnit requireUnit;

    public LimiterException(Object key, long last, TimeUnit lastUnit, long requireTime, TimeUnit requireUnit) {
        super(String.format("频率过高，请在 %d 秒后重试", lastUnit.toSeconds(last)));
        this.key = key;
        this.last = last;
        this.lastUnit = lastUnit;
        this.requireTime = requireTime;
        this.requireUnit = requireUnit;
    }

    public Object getKey() {
        return key;
    }

    public long getLast() {
        return last;
    }

    public TimeUnit getLastUnit() {
        return lastUnit;
    }

    public long getRequireTime() {
        return requireTime;
    }

    public TimeUnit getRequireUnit() {
        return requireUnit;
    }
}
