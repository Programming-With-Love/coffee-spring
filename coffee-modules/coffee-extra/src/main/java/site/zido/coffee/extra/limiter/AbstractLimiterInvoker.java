package site.zido.coffee.extra.limiter;

/**
 * aop调用invoker
 *
 * @author zido
 */
public class AbstractLimiterInvoker {
    private LimiterErrorHandler errorHandler;

    protected AbstractLimiterInvoker() {
        this(new SimpleLimiterErrorHandler());
    }

    protected AbstractLimiterInvoker(LimiterErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public LimiterErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(LimiterErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
}
