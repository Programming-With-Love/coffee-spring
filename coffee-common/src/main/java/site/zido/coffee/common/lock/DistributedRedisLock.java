package site.zido.coffee.common.lock;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.util.Assert;
import site.zido.coffee.common.utils.IdWorker;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁,基于redis实现,非公平锁，不可重入锁
 *
 * @author zido
 */
public class DistributedRedisLock extends AbstractDistributedLock implements Serializable, InitializingBean {
    private static final long serialVersionUID = -8954727144655510783L;
    private static final byte[] LOCK_SUCCESS = {79, 75};
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Charset USE_CHARSET = StandardCharsets.UTF_8;
    private static final String DEL_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
    private static final String ADD_SCRIPT = "if redis.call('get', KEYS[1]) ~= ARGV[1] then return redis.call('set', KEYS[1], ARGV[1], ARGV[2], ARGV[3]) else return 0 end";
    private static final byte[] DEL_SCRIPT_BYTES = DEL_SCRIPT.getBytes(USE_CHARSET);
    private static final byte[] ADD_SCRIPT_BYTES = ADD_SCRIPT.getBytes(USE_CHARSET);
    private static final byte[] SET_WITH_EXPIRE_TIME_BYTES = SET_WITH_EXPIRE_TIME.getBytes(USE_CHARSET);

    private final String key;
    private long timeout;
    private TimeUnit unit;
    private transient byte[] keyBytes;
    private transient byte[] timeoutBytes;
    private transient volatile boolean initialized = false;
    private RedisConnectionFactory connectionFactory;
    private byte[] value;

    /**
     * 默认情况下采用spring方式管理lock,也就是由spring负责回收生命周期
     *
     * @param key               key
     * @param connectionFactory redis 连接
     * @param timeout           timeout
     * @param unit              timeunit
     */
    public DistributedRedisLock(String key,
                                RedisConnectionFactory connectionFactory,
                                long timeout,
                                TimeUnit unit) {
        this(key, connectionFactory, timeout, unit, true);
    }

    public DistributedRedisLock(String key,
                                RedisConnectionFactory connectionFactory,
                                long timeout,
                                TimeUnit unit,
                                boolean isSpringBean) {
        super(isSpringBean);
        this.connectionFactory = connectionFactory;
        this.key = key;
        this.timeout = timeout;
        this.unit = unit;
    }

    private void initValue() {
        RedisConnection connection = RedisConnectionUtils.getConnection(connectionFactory);
        try {
            this.value = connection.get(keyBytes);
            if (null == this.value || this.value.length == 0) {
                this.value = (IdWorker.nextId() + "").getBytes(USE_CHARSET);
            }
        } finally {
            RedisConnectionUtils.releaseConnection(connection, connectionFactory);
        }
    }

    @Override
    public boolean doTryLock() {
        Assert.isTrue(initialized, "lock not initialized; call afterPropertiesSet() before using it");
        byte[][] keysAndArgs = new byte[4][];
        keysAndArgs[0] = this.keyBytes;
        keysAndArgs[1] = this.value;
        keysAndArgs[2] = SET_WITH_EXPIRE_TIME_BYTES;
        keysAndArgs[3] = timeoutBytes;
        RedisConnection connection = RedisConnectionUtils.getConnection(connectionFactory);
        try {
            //结果应该是OK字符串的byte数组
            Object result = connection.eval(ADD_SCRIPT_BYTES, ReturnType.INTEGER, 1, keysAndArgs);
            if (result instanceof byte[]) {
                return Arrays.equals(LOCK_SUCCESS, (byte[]) result);
            }
            return false;
        } finally {
            RedisConnectionUtils.releaseConnection(connection, connectionFactory);
        }
    }

    @Override
    public void doUnlock() {
        Assert.isTrue(initialized, "lock not initialized; call afterPropertiesSet() before using it");
        //结果可能是0或者1，但是不需要有失败判定
        byte[][] keysAndArgs = new byte[2][];
        keysAndArgs[0] = this.keyBytes;
        keysAndArgs[1] = this.value;
        RedisConnection connection = RedisConnectionUtils.getConnection(connectionFactory);
        try {
            connection.eval(DEL_SCRIPT_BYTES, ReturnType.VALUE, 1, keysAndArgs);
        } finally {
            RedisConnectionUtils.releaseConnection(connection, connectionFactory);
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.keyBytes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DistributedRedisLock that = (DistributedRedisLock) o;
        return Arrays.equals(keyBytes, that.keyBytes);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.hasLength(this.key, "key can't be blank");
        Assert.notNull(connectionFactory, "redis connection factory can't be null");
        this.keyBytes = key.getBytes(USE_CHARSET);
        this.timeoutBytes = (unit.toMillis(timeout) + "").getBytes(USE_CHARSET);
        initValue();
        initialized = true;
    }
}
