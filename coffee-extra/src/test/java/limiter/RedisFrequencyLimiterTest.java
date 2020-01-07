package limiter;

import site.zido.coffee.extra.limiter.RedisFrequencyLimiter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RedisFrequencyLimiterTest {
    private RedisTemplate<String, Long> template;
    private RedisServer redisServer;

    @Before
    public void init() throws IOException {
        redisServer = new RedisServer(6380);
        redisServer.start();
        template = new RedisTemplate<>();
        JedisPoolConfig config = new JedisPoolConfig();
        JedisConnectionFactory factory = new JedisConnectionFactory(config);
        factory.getStandaloneConfiguration().setPort(6380);
        template.setConnectionFactory(factory);
        template.afterPropertiesSet();
    }

    @After
    public void destroy() {
        redisServer.stop();
    }

    @Test
    public void test() throws InterruptedException {
        RedisFrequencyLimiter limiter = new RedisFrequencyLimiter(template);
        String key = "test-one";
        int timeout = 1;
        long millis = TimeUnit.SECONDS.toMillis(timeout);
        for (int i = 0; i < 3; i++) {
            Assert.assertTrue(limiter.tryGet(key, timeout, TimeUnit.SECONDS));
            Assert.assertFalse(limiter.tryGet(key, timeout, TimeUnit.SECONDS));
            Thread.sleep(millis);
        }
    }
}
