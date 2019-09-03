package site.zido.coffee.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import site.zido.coffee.auth.config.AutoSwitchSourceRegistry;

/**
 * 认证相关自动配置
 *
 * @author zido
 */
@Configuration
@Import(AutoSwitchSourceRegistry.class)
public class CoffeeAuthAutoConfiguration {
}
