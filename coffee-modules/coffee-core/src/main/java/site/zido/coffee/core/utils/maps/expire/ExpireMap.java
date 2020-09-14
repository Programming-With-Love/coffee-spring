package site.zido.coffee.core.utils.maps.expire;

import net.jcip.annotations.ThreadSafe;
import site.zido.coffee.core.utils.DebounceExecutor;

import java.util.Iterator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 线程安全的缓存容器，过期时间单位为毫秒
 *
 * <ul>
 *     <li>类redis指令</li>
 *     <li>内存</li>
 *     <li>线程安全</li>
 *     <li>过期删除</li>
 * </ul>
 *
 * @param <K>
 * @param <V>
 */
@ThreadSafe
public class ExpireMap<K, V> {

    /**
     * 内存释放延迟时间
     * <p>
     * 默认1s
     */
    private static final long DEFAULT_RELEASE_FACTOR = 1000;

    /**
     * 计算是否过期
     */
    private final SortedSet<SortedKey<K>> sortedKeys = new TreeSet<>();
    /**
     * 存储存储过的需要过期的Key，用于索引SortedSet
     */
    private final transient ConcurrentHashMap<K, SortedKey<K>> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<K, V> valContainer = new ConcurrentHashMap<>();

    private final DebounceExecutor executor;

    public ExpireMap() {
        this(DEFAULT_RELEASE_FACTOR);
    }

    public ExpireMap(long releaseFactor) {
        executor = new DebounceExecutor(this::releaseMemory, releaseFactor);
    }

    /**
     * 获取过期时间
     *
     * @param key key
     * @return val
     */
    public long ttl(K key) {
        tickReleaseMemory();
        SortedKey<K> val;
        if ((val = cache.get(key)) == null) {
            return 0;
        }
        return returnExpireTime(val.expireTime);
    }

    private long returnExpireTime(long expireTime) {
        if (expireTime == -1) {
            return -1;
        }
        long diff;
        return (diff = expireTime - System.currentTimeMillis()) > 0 ? diff : 0;
    }

    /**
     * 放入缓存
     *
     * @param key     key
     * @param val     val
     * @param timeout 过期时间
     */
    public void set(K key, V val, long timeout) {
        tickReleaseMemory();
        if (timeout < 0) {
            cache.put(key, new SortedKey<>(key, -1L));
            return;
        }
        if (timeout == 0) {
            return;
        }
        long expireTime = timeout + System.currentTimeMillis();
        cache.compute(key, (k, v) -> {
            if (v != null) {
                sortedKeys.remove(v);
            }
            v = new SortedKey<>(key, expireTime);
            sortedKeys.add(v);
            valContainer.put(key, val);
            return v;
        });
    }

    /**
     * 放入缓存，长期有效
     *
     * @param key key
     * @param val value
     */
    public void set(K key, V val) {
        set(key, val, -1L);
    }

    /**
     * 如果缓存中没有，则设置，否则返回
     *
     * @param key key
     * @param val value
     * @return 过期时间
     */
    public long setNx(K key, V val, long timeout) {
        SortedKey<K> sortedKey = cache.compute(key, (k, v) -> {
            if (v == null) {
                long expireTime = timeout + System.currentTimeMillis();
                v = new SortedKey<>(key, expireTime);
                sortedKeys.add(v);
                valContainer.put(key, val);
            }
            return v;
        });
        return returnExpireTime(sortedKey.expireTime);
    }

    /**
     * 获取数据
     *
     * @param key key
     * @return value
     */
    public V get(K key) {
        tickReleaseMemory();
        long crt = System.currentTimeMillis();
        SortedKey<K> compute = cache.compute(key, (k, v) -> {
            if (v == null
                    || (v.expireTime != -1 && v.expireTime - crt <= 0)) {
                return null;
            }
            return v;
        });
        if (compute != null) {
            return valContainer.get(compute.key);
        }
        return null;
    }

    /**
     * 调用后会选择合适的时机进行内存释放
     * <p>
     * 防止每次执行都会使用内存释放从而导致性能下降
     */
    public void tickReleaseMemory() {
        executor.run();
    }

    public void releaseMemory() {
        long crt = System.currentTimeMillis();
        Iterator<SortedKey<K>> iter = sortedKeys.iterator();
        while (iter.hasNext()) {
            SortedKey<K> item = iter.next();
            if (item.expireTime <= crt) {
                //一定要优先删除cache，因为所有的查询都是从cache开始，当cache的key不存在，也就不会动用其他容器
                //cache使用transient防止此处被重排序
                cache.remove(item.key);
                iter.remove();
                valContainer.remove(item.key);
            }
        }
    }

    protected static class SortedKey<K> implements Comparable<SortedKey<K>> {
        /**
         * 键
         */
        private final K key;
        /**
         * 过期时间戳
         */
        private final Long expireTime;

        public SortedKey(K key, long expireTime) {
            this.key = key;
            this.expireTime = expireTime;
        }

        @Override
        public int compareTo(SortedKey o) {
            return Long.compare(o.expireTime, expireTime);
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SortedKey<K> sortedKey = (SortedKey<K>) o;
            return Objects.equals(key, sortedKey.key) &&
                    Objects.equals(expireTime, sortedKey.expireTime);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, expireTime);
        }
    }
}
