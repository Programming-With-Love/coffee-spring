package site.zido.coffee.common.utils;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.Jedis;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 分布式锁,基于redis实现,非公平锁
 *
 * @author zido
 */
public class DistributedRedisLock implements Lock, Serializable {
    private static final long serialVersionUID = -8954727144655510783L;
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final String DEL_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
    private static final String ADD_SCRIPT = "if redis.call('get', KEYS[1]) ~= ARGV[1] then return redis.call('set', KEYS[1], ARGV[1], ARGV[2], ARGV[3]) else return 0 end";
    private String key;
    private Jedis jedis;
    private long timeout;
    private TimeUnit unit;
    private String value;

    public DistributedRedisLock(String key, Jedis jedis) {
        this(key, jedis, 100, TimeUnit.SECONDS);
    }

    public DistributedRedisLock(String key, StringRedisTemplate template) {
        this(key, template, 100, TimeUnit.SECONDS);
    }

    public DistributedRedisLock(String key, Jedis jedis, long timeout, TimeUnit unit) {
        this.key = key;
        this.jedis = jedis;
        this.timeout = timeout;
        this.unit = unit;
        initValue();
    }

    public DistributedRedisLock(String key, StringRedisTemplate template, long timeout, TimeUnit unit) {
        final RedisConnection connection = template.getConnectionFactory().getConnection();
        if (!(connection instanceof JedisConnection)) {
            throw new UnsupportedOperationException("except jedis");
        }
        this.jedis = ((JedisConnection) connection).getNativeConnection();
        this.key = key;
        this.timeout = timeout;
        this.unit = unit;
        initValue();
    }

    private void initValue() {
        this.value = jedis.get(key);
        if (null == this.value || "".equals(this.value)) {
            this.value = IdWorker.nextId() + "";
        }
    }

    @Override
    public void lock() {
        try {
            lockInterruptibly();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        while (!tryLock()) {
            TimeUnit.NANOSECONDS.sleep(5);
        }
    }

    @Override
    public boolean tryLock() {
        final Object eval = jedis.eval(ADD_SCRIPT, Collections.singletonList(key), Arrays.asList(value, SET_WITH_EXPIRE_TIME, "" + unit.toMillis(timeout)));
        return LOCK_SUCCESS.equals(eval);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        final long timeout = unit.toNanos(time);
        final long deadline = System.nanoTime() + timeout;
        for (; ; ) {
            if (tryLock()) {
                return true;
            }
            time = deadline - System.nanoTime();
            if (time <= 0L) {
                return false;
            }
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
    }

    @Override
    public void unlock() {
        //结果可能是0或者1，但是不需要有失败判定
        jedis.eval(DEL_SCRIPT, Collections.singletonList(key), Collections.singletonList(value));
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("not support condition");
    }
}
