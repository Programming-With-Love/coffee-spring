package site.zido.coffee.extra.limiter;

import java.util.concurrent.TimeUnit;

/**
 * 节点频率限制器，限制某个用户（key区分）的某个动作在特定时间内只能有一次
 *
 * @author zido
 */
public interface FrequencyLimiter {
    /**
     * 尝试执行
     *
     * @param key     key
     * @param timeout 设置如果本次拿到执行权的有效时间
     * @param unit    单位
     * @return 如果拿到了执行权则返回true, 否则返回false
     */
    default boolean tryGet(String key, long timeout, TimeUnit unit) {
        return tryGetForItem(key, timeout, unit) == null;
    }

    /**
     * 尝试执行
     *
     * @param key     key
     * @param timeout 设置如果本次拿到执行权的有效时间
     * @param unit    单位
     * @return 如果拿到了执行权则返回null，否则返回还需等待的时间
     */
    LastItem tryGetForItem(String key, long timeout, TimeUnit unit);

    final class LastItem {
        private long time;
        private TimeUnit unit;

        LastItem(long time, TimeUnit unit) {
            this.time = time;
            this.unit = unit;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public TimeUnit getUnit() {
            return unit;
        }

        public void setUnit(TimeUnit unit) {
            this.unit = unit;
        }
    }
}
