package site.zido.coffee.extra.limiter;

/**
 * @author zido
 */
public class SimpleLimiterErrorHandler implements LimiterErrorHandler {
    @Override
    public void handleError(RuntimeException exception, Object key) {
        throw exception;
    }

    @Override
    public void handleOnLimited(LimiterException exception) {
        throw exception;
    }
}
