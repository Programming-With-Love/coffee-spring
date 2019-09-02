package site.zido.coffee.auth.config;

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

@Order(Ordered.HIGHEST_PRECEDENCE)
public class AutoSwitchSourceRegistry implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean inJpa = false;
        try {
            ClassUtils.getDefaultClassLoader().loadClass("org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean");
            inJpa = true;
        } catch (ClassNotFoundException ignore) {
        }
        String registrarBeanName = "authClassAutoRegistrar";
        if (inJpa) {
            registry.registerBeanDefinition("jpaAutoRegister",
                    BeanDefinitionBuilder.rootBeanDefinition(RegisterJpaAutoRegister.class)
                            .getBeanDefinition());
            AbstractBeanDefinition autoRegister = BeanDefinitionBuilder.rootBeanDefinition(JpaAutoRegister.class)
                    .setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
                    .getBeanDefinition();
            registry.registerBeanDefinition(registrarBeanName, autoRegister);

            AbstractBeanDefinition commonConfiguration = BeanDefinitionBuilder.rootBeanDefinition(AuthCommonConfiguration.class)
                    .setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
                    .getBeanDefinition();
            registry.registerBeanDefinition("commonConfiguration", commonConfiguration);
        }

    }
}
