package site.zido.coffee.auth.config.jpa;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import site.zido.coffee.auth.config.jpa.JpaAutoRegister;

@Configuration
@ConditionalOnClass(LocalContainerEntityManagerFactoryBean.class)
public
class RegisterJpaAutoRegister implements BeanPostProcessor {
    private JpaAutoRegister register;

    RegisterJpaAutoRegister(JpaAutoRegister register) {
        this.register = register;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof LocalContainerEntityManagerFactoryBean) {
            ((LocalContainerEntityManagerFactoryBean) bean).setPersistenceUnitPostProcessors(register);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
