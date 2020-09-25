package site.zido.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

/**
 * 启动器
 * <p>
 * 本模块是完全采取默认规则进行集成的项目
 * <p>
 * 完全与spring boot同样的使用，能带来更多的restful配置
 *
 * @author zido
 */
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(DemoApplication.class);
    }
}
