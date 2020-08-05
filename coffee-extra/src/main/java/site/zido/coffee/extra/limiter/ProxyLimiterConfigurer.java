package site.zido.coffee.extra.limiter;

/**
 * limiter配置
 */
public interface ProxyLimiterConfigurer {
    FrequencyLimiter limiter();
}
