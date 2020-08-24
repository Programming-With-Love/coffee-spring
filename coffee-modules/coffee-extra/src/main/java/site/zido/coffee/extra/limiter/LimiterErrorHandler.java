package site.zido.coffee.extra.limiter;

/**
 * 异常处理器
 *
 * @author zido
 */
public interface LimiterErrorHandler {
    /**
     * 处理通用异常
     *
     * @param exception exception
     * @param key       注解的key
     */
    void handleError(RuntimeException exception, Object key);

    /**
     * 被限流时的处理
     *
     * @param exception exception
     */
    void handleOnLimited(LimiterException exception);
}
