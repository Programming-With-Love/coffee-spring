package site.zido.demo.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import redis.embedded.RedisServer;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * 配置一个内存redis链接
 *
 * @author zido
 */
@Configuration
public class RedisConfiguration implements InitializingBean {

    private RedisServer server;
    private int port;

    public RedisConfiguration() throws IOException {
        ServerSocket serverSocket = new ServerSocket(0);
        port = serverSocket.getLocalPort();
        serverSocket.close();
        server = new RedisServer(port);
    }

    public void startServer() {
        server.start();
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
        server.stop();
    }
}
