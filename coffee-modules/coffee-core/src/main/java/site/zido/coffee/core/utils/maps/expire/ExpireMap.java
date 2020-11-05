package site.zido.coffee.core.utils.maps.expire;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;

import net.jcip.annotations.ThreadSafe;

/**
 * 线程安全的缓存容器，过期时间单位为毫秒
 *
 * <ul>
 * <li>类redis指令</li>
 * <li>内存</li>
 * <li>线程安全</li>
 * <li>过期删除</li>
 * </ul>
 *
 * @param <K>
 * @param <V>
 */
@ThreadSafe
public class ExpireMap<K, V> {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * 计算是否过期
     */
    private final SortedSet<SortedKey<K>> sortedKeys = new TreeSet<>();
    /**
     * 存储存储过的需要过期的Key，用于索引SortedSet
     */
    private final HashMap<K, SortedKey<K>> cache = new HashMap<>();
    private final HashMap<K, V> valContainer = new HashMap<>();

    private final long releaseIntervalTime;

    private final AtomicLong lastRelease;

    public ExpireMap() {
        this(1, TimeUnit.SECONDS);
    }

    public ExpireMap(long releaseIntervalTime, TimeUnit unit) {
        this.releaseIntervalTime = unit.toMillis(releaseIntervalTime);
        lastRelease = new AtomicLong(System.currentTimeMillis());
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
        lock.readLock().lock();
        try {
            if ((val = cache.get(key)) == null) {
                return 0;
            }
        } finally {
            lock.readLock().unlock();
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

    private boolean expired(SortedKey<K> v) {
        if (v == null) {
            return true;
        }
        if (v.expireTime != -1 && v.expireTime - System.currentTimeMillis() <= 0) {
            sortedKeys.remove(v);
            valContainer.remove(v.key);
            return true;
        }
        return false;
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
        lock.writeLock().lock();
        try {
            cache.compute(key, put(key, val, timeout));
        } finally {
            lock.writeLock().unlock();
        }
    }

    private BiFunction<K, SortedKey<K>, SortedKey<K>> put(K key, V val, long timeout) {
        return (k, v) -> {
            if (v != null) {
                sortedKeys.remove(v);
            }
            if (timeout < 0) {
                valContainer.put(k, val);
                return new SortedKey<>(key, -1L);
            }
            // 如果timeout == 0 代表删除
            if (timeout == 0) {
                valContainer.remove(k);
                return null;
            }
            long expireTime = timeout + System.currentTimeMillis();
            v = new SortedKey<>(k, expireTime);
            sortedKeys.add(v);
            valContainer.put(k, val);
            return v;
        };
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
     * 如果缓存中没有，则设置成功，否则失败
     *
     * @param key key
     * @param val value
     * @return 过期时间
     */
    public boolean setNx(K key, V val, long timeout) {
        tickReleaseMemory();
        lock.writeLock().lock();
        boolean[] success = new boolean[] { false };
        try {
            cache.compute(key, (k, v) -> {
                if (expired(v)) {
                    v = put(k, val, timeout).apply(k, v);
                    success[0] = true;
                }
                return v;
            });
        } finally {
            lock.writeLock().unlock();
        }
        return success[0];
    }

    /**
     * 获取数据
     *
     * @param key key
     * @return value
     */
    @SuppressWarnings("unchecked")
    public V get(K key) {
        tickReleaseMemory();
        Object[] result = new Object[] { null };
        lock.readLock().lock();
        try {
            cache.compute(key, (k, v) -> {
                if (expired(v)) {
                    result[0] = null;
                } else {
                    result[0] = valContainer.get(k);
                }
                return v;
            });
        } finally {
            lock.readLock().unlock();
        }
        return (V) result[0];
    }

    /**
     * 调用后会选择合适的时机进行内存释放
     * <p>
     * 防止每次执行都会使用内存释放从而导致性能下降
     */
    public void tickReleaseMemory() {
        long crt = System.currentTimeMillis();
        if (crt - lastRelease.get() > releaseIntervalTime) {
            lock.writeLock().lock();
            try {
                if (crt - lastRelease.get() > releaseIntervalTime) {
                    releaseMemory();
                }
                lastRelease.set(System.currentTimeMillis());
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    private void releaseMemory() {
        long crt = System.currentTimeMillis();
        Iterator<SortedKey<K>> iter = sortedKeys.iterator();
        while (iter.hasNext()) {
            SortedKey<K> item = iter.next();
            if (item.expireTime <= crt) {
                cache.compute(item.key, (k, kSortedKey) -> {
                    iter.remove();
                    valContainer.remove(item.key);
                    return null;
                });
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
        public int compareTo(SortedKey<K> o) {
            return Long.compare(o.expireTime, expireTime);
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            SortedKey<K> sortedKey = (SortedKey<K>) o;
            return Objects.equals(key, sortedKey.key) && Objects.equals(expireTime, sortedKey.expireTime);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, expireTime);
        }
    }
}
