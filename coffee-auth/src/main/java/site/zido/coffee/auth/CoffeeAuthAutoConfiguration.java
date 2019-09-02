package site.zido.coffee.auth;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import site.zido.coffee.auth.config.AutoSwitchSourceRegistry;
import site.zido.coffee.auth.config.jpa.JpaAutoRegister;

/**
 * 认证相关自动配置
 *
 * @author zido
 */
@Configuration
@Import(AutoSwitchSourceRegistry.class)
public class CoffeeAuthAutoConfiguration {


}
