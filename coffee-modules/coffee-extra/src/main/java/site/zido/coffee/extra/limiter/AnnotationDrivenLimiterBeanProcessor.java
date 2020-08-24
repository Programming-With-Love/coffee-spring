package site.zido.coffee.extra.limiter;

import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 向容器中注入PointcutAdvisor切点{@link BeanFactoryLimiterOperationSourceAdvisor}
 *
 * @author zido
 */
public class AnnotationDrivenLimiterBeanProcessor implements ImportBeanDefinitionRegistrar {
    private static final String LIMITER_ASPECT_CLASS_NAME = "site.zido.common.AnnotationLimiterAspect";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
        RootBeanDefinition beanDefinition = new RootBeanDefinition(BeanFactoryLimiterOperationSourceAdvisor.class);
        //设置标识为服务
        beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition(LIMITER_ASPECT_CLASS_NAME, beanDefinition);
    }
}
