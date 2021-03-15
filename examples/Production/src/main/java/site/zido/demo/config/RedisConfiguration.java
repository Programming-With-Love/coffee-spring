package site.zido.demo.config;

import ai.grakn.redismock.RedisServer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * 配置一个内存redis链接
 *
 * @author zido
 */
@Configuration
public class RedisConfiguration implements InitializingBean {

    private static RedisServer redisServer = null;
    private int port;

    public RedisConfiguration() throws IOException {
        redisServer = RedisServer.newRedisServer();
    }

    public void startServer() throws IOException {
        redisServer.start();
        port = redisServer.getBindPort();
    }

    @Bean
    public RedisConnectionFactory factory() {
        return new LettuceConnectionFactory("127.0.0.1", port);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        startServer();
    }

    @PreDestroy
    public void destroyServer() {
        redisServer.stop();
    }
}
