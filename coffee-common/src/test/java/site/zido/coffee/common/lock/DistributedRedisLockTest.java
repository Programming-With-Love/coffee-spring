package site.zido.coffee.common.lock;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.impl.SimpleLogger;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class DistributedRedisLockTest {
    private RedisServer redisServer;

    @Before
    public void init() throws IOException {
        redisServer = new RedisServer(6380);
        redisServer.start();
    }

    @After
    public void destroy() {
        redisServer.stop();
    }

    @Test
    public void testLock() {
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
        JedisPoolConfig config = new JedisPoolConfig();
        JedisConnectionFactory factory = new JedisConnectionFactory(config);
        factory.setPort(6380);
        factory.afterPropertiesSet();
        DistributedRedisLock lock = new DistributedRedisLock("test", factory, 10, TimeUnit.SECONDS,false);
        lock.lock();
        //加锁后，无法再次拿到锁
        Assert.assertFalse(lock.tryLock());
        lock.unlock();
        //解锁后能够正常拿到锁
        Assert.assertTrue(lock.tryLock());
        DistributedRedisLock.releaseAll();
    }
}
