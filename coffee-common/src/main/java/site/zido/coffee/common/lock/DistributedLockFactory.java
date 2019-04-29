package site.zido.coffee.common.lock;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;

public class DistributedLockFactory {
    private Map<MultiKey,Lock> lockCache = new ConcurrentHashMap<>();
    private RedisConnectionFactory redisConnectionFactory;
    private Charset charset;
    private IdCreator idCreator;

    public RedisConnectionFactory getRedisConnectionFactory() {
        return redisConnectionFactory;
    }

    public void setRedisConnectionFactory(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public void setIdCreator(IdCreator idCreator) {
        this.idCreator = idCreator;
    }

    public IdCreator getIdCreator() {
        return idCreator;
    }

    public interface IdCreator {
        String create();
    }

    public Lock getLock(String key, long timeout, TimeUnit unit){
        return lockCache.computeIfAbsent(new MultiKey(key, timeout, unit), multiKey -> {
            DistributedRedisLock lock = new DistributedRedisLock(key, redisConnectionFactory, timeout, unit);
            lock.setIdCreator(DistributedLockFactory.this.idCreator);
            return lock;
        });
    }

    static class MultiKey {
        private String key;
        private long timeout;
        private TimeUnit unit;

        MultiKey(String key, long timeout, TimeUnit unit) {
            Assert.hasLength(key,"lock key can't be null or empty");
            Assert.state(timeout > 0,"超时时间还必须大于0");
            this.key = key;
            this.timeout = timeout;
            this.unit = unit;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MultiKey multiKey = (MultiKey) o;
            return timeout == multiKey.timeout &&
                    Objects.equals(key, multiKey.key) &&
                    unit == multiKey.unit;
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, timeout, unit);
        }
    }
}
