package site.zido.coffee.extra.limiter;

/**
 * 节点频率限制器，限制某个用户（key区分）的某个动作在特定时间内只能有一次
 * <p>
 * 秒为单位
 *
 * @author zido
 */
public interface FrequencyLimiter {

    /**
     * 尝试执行
     *
     * @param key     key
     * @param timeout 设置如果本次拿到执行权的有效时间
     * @return 如果拿到了执行权则返回0，否则返回还需等待的时间
     */
    long tryGet(String key, long timeout);
}
