package site.zido.coffee.extra.limiter;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的频率限制
 *
 * @author zido
 */
@ThreadSafe
public class MemoryFrequencyLimiter implements FrequencyLimiter {
    private final SortedSet<SortedKey> sortedKeys = new TreeSet<>();
    private final Map<String, SortedKey> cache = new ConcurrentHashMap<>();
    private String prefix = "";

    public MemoryFrequencyLimiter() {

    }

    public MemoryFrequencyLimiter(String prefix) {
        this.prefix = prefix;
    }

    @Override
    @GuardedBy("cache")
    public long tryGet(String key, long timeout) {
        key = getKey(key);
        //进一，确保过失时间不会短于约定时间
        long crt = System.currentTimeMillis() / 1000 + 1;
        SortedKey sortedKey = new SortedKey(key, crt + timeout);
        SortedKey target = cache.compute(key, (k, v) -> {
            //如果原k不存在，一定可以申请。如果不存在，可能是还没来得及回收，需要判断过期时间戳
            if (v == null || v.timeout - crt <= 0) {
                sortedKeys.add(sortedKey);
                return sortedKey;
            }
            return v;
        });
        releaseMemory();
        if (sortedKey == target) {
            return 0;
        }
        return target.timeout - crt;
    }

    protected String getKey(String key) {
        return prefix + key;
    }

    public void releaseMemory() {
        long crt = System.currentTimeMillis();
        Iterator<SortedKey> iter = sortedKeys.iterator();
        while (iter.hasNext()) {
            SortedKey item = iter.next();
            if (item.timeout > crt) {
                cache.remove(item.key);
                iter.remove();
            }
        }
    }

    private static class SortedKey implements Comparable<SortedKey> {
        private final String key;
        private final Long timeout;

        public SortedKey(String key, Long timeout) {
            this.key = key;
            this.timeout = timeout;
        }

        @Override
        public int compareTo(SortedKey o) {
            return (int) (o.timeout - timeout);
        }
    }
}
