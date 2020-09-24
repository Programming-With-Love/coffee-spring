package site.zido.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import site.zido.coffee.mvc.logger.EnableRequestLogger;

import java.io.IOException;

/**
 * 启动器
 *
 * @author zido
 */
@SpringBootApplication
@EnableRequestLogger
public class DemoApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(DemoApplication.class);
    }
}
