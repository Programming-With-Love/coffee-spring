package site.zido.coffee.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import site.zido.coffee.auth.AuthCommonConfiguration;
import site.zido.coffee.auth.config.jpa.JpaAutoRegister;
import site.zido.coffee.auth.config.jpa.RegisterJpaAutoRegister;

import java.util.List;
import java.util.function.Consumer;

/**
 * 根据自动配置包来检测当前注册环境，从而进行自动配置
 * <p>
 * 高优先级是为了在jpa自动配置之前执行插入{@link JpaAutoRegister}，从而适应jpa环境所做的自动扫描配置
 *
 * @author zido
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AutoSwitchSourceRegistry implements ImportBeanDefinitionRegistrar {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoSwitchSourceRegistry.class);

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean inJpa = false;
        //查询jpa环境
        try {
            ClassUtils.getDefaultClassLoader()
                    .loadClass("org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean");
            inJpa = true;
        } catch (ClassNotFoundException ignore) {
            //非jpa环境
        }
        String registrarBeanName = "authClassAutoRegistrar";
        //如果在jpa环境中执行此注册逻辑
        if (inJpa) {
            LOGGER.debug("auth spring data jpa environment");
            AbstractBeanDefinition autoRegister = BeanDefinitionBuilder
                    .rootBeanDefinition(JpaAutoRegister.class)
                    .setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
                    .getBeanDefinition();
            registry.registerBeanDefinition(registrarBeanName, autoRegister);
            registry.registerBeanDefinition("jpaAutoRegister",
                    BeanDefinitionBuilder.rootBeanDefinition(RegisterJpaAutoRegister.class)
                            .getBeanDefinition());
            AbstractBeanDefinition commonConfiguration = BeanDefinitionBuilder.rootBeanDefinition(AuthCommonConfiguration.class)
                    .setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
                    .getBeanDefinition();
            registry.registerBeanDefinition("commonConfiguration", commonConfiguration);
            return;
        }
        LOGGER.debug("auth in other environment without spring data jpa");
        //预留非jpa环境自动配置
    }
}
