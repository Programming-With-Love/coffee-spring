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
 * 是否实现可重入锁需要讨论，涉及到分布式调用时需要传播
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
    private static final DistributedLockFactory.IdCreator DEFAULT_ID_CREATER = () -> IdWorker.nextId() + "";

    private final String key;
    private final byte[] key_bytes;
    private final byte[] timeout_bytes;
    private DistributedLockFactory.IdCreator idCreator = DEFAULT_ID_CREATER;
    private RedisConnectionFactory connectionFactory;
    private byte[] value;

    public DistributedRedisLock(String key,
                                RedisConnectionFactory connectionFactory,
                                long timeout,
                                TimeUnit unit) {
        this(key,connectionFactory,timeout,unit,true);
    }

    public DistributedRedisLock(String key,
                                RedisConnectionFactory connectionFactory,
                                long timeout,
                                TimeUnit unit,
                                boolean isSpringBean) {
        super(isSpringBean);
        this.connectionFactory = connectionFactory;
        this.key = key;
        this.key_bytes = key.getBytes(USE_CHARSET);
        this.timeout_bytes = (unit.toMillis(timeout) + "").getBytes();
        initValue();
    }

    private void initValue() {
        RedisConnection connection = RedisConnectionUtils.getConnection(connectionFactory);
        try {
            this.value = connection.get(key_bytes);
            if (null == this.value || this.value.length == 0) {
                this.value = idCreator.create().getBytes(USE_CHARSET);
            }
        } finally {
            RedisConnectionUtils.releaseConnection(connection, connectionFactory);
        }
    }

    @Override
    public boolean doTryLock() {
        byte[][] keysAndArgs = new byte[4][];
        keysAndArgs[0] = this.key_bytes;
        keysAndArgs[1] = this.value;
        keysAndArgs[2] = SET_WITH_EXPIRE_TIME_BYTES;
        keysAndArgs[3] = timeout_bytes;
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
        //结果可能是0或者1，但是不需要有失败判定
        byte[][] keysAndArgs = new byte[2][];
        keysAndArgs[0] = this.key_bytes;
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
        return Arrays.hashCode(this.key_bytes);
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
        return Arrays.equals(key_bytes, that.key_bytes);
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setIdCreator(DistributedLockFactory.IdCreator idCreator) {
        this.idCreator = idCreator;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.key_bytes == null || this.key_bytes.length == 0) {
            throw new IllegalArgumentException("key can't be null or blank");
        }
        if (this.value == null || this.value.length == 0) {
            throw new IllegalArgumentException("value can't be null or blank");
        }
        Assert.notNull(connectionFactory, "redis connection factory can't be null");
        Assert.notNull(idCreator,"id creator can't be null");
    }
}
