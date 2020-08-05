package site.zido.coffee.extra.limiter;

/**
 * 当方法调用频率被限制时，抛出的异常
 *
 * @author zido
 * @see LimiterErrorHandler
 * @see FrequencyLimiter
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
    /**
     * 下次需要的时间
     */
    private long requireTime;

    public LimiterException() {

    }

    public LimiterException(Object key, long last, long requireTime) {
        super(String.format("频率过高，请在 %d 秒后重试", last));
        this.key = key;
        this.last = last;
        this.requireTime = requireTime;
    }

    public Object getKey() {
        return key;
    }

    public long getLast() {
        return last;
    }

    public long getRequireTime() {
        return requireTime;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public void setLast(long last) {
        this.last = last;
    }

    public void setRequireTime(long requireTime) {
        this.requireTime = requireTime;
    }
}
