package lock;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.impl.SimpleLogger;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;
import redis.embedded.RedisServer;
import site.zido.coffee.extra.lock.DistributedRedisLock;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class DistributedRedisLockTest {
    private static RedisServer redisServer;
    private static JedisConnectionFactory factory;

    @BeforeClass
    public static void init() throws IOException {
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
        redisServer = new RedisServer(6381);
        redisServer.start();
        JedisPoolConfig config = new JedisPoolConfig();
        factory = new JedisConnectionFactory(config);
        factory.setPort(6381);
        factory.afterPropertiesSet();
    }

    @AfterClass
    public static void destroy() {
        DistributedRedisLock.releaseAll();
        factory.destroy();
        redisServer.stop();
    }

    @Test
    public void testLock() {
        DistributedRedisLock lock = new DistributedRedisLock("test", factory, 10, TimeUnit.SECONDS, false);
        lock.afterPropertiesSet();
        lock.lock();
        //加锁后，无法再次拿到锁
        Assert.assertFalse(lock.tryLock());
        lock.unlock();
        //解锁后能够正常拿到锁
        Assert.assertTrue(lock.tryLock());
        DistributedRedisLock.releaseAll();
    }

    @Test
    public void testMultiKeyLock() {
        DistributedRedisLock lock1 = new DistributedRedisLock("test", factory, 10, TimeUnit.SECONDS, false);
        DistributedRedisLock lock2 = new DistributedRedisLock("test", factory, 10, TimeUnit.SECONDS, false);
        lock1.afterPropertiesSet();
        lock2.afterPropertiesSet();
        lock1.lock();
        Assert.assertFalse(lock1.tryLock());
        lock1.unlock();
        Assert.assertTrue(lock2.tryLock());
    }
}
