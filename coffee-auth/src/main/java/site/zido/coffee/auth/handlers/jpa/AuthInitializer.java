package site.zido.coffee.auth.handlers.jpa;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.util.ClassUtils;
import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.handlers.AuthHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AuthInitializer implements InitializingBean, BeanFactoryAware {
    private BeanFactory beanFactory;
    private Map<Class<? extends IUser>, AuthHandler> handlerMap;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, JpaRepositoryFactoryBean> jpaRepositoryFactoryBeanMap = BeanFactoryUtils.beansOfTypeIncludingAncestors((ListableBeanFactory) beanFactory, JpaRepositoryFactoryBean.class);
        Map<Class<? extends IUser>, AuthHandler> map = new HashMap<>();
        for (JpaRepositoryFactoryBean factoryBean : jpaRepositoryFactoryBeanMap.values()) {
            Class javaType = factoryBean.getEntityInformation().getJavaType();
            if (ClassUtils.isAssignable(javaType, IUser.class)) {
                JpaRepository repository = (JpaRepository) factoryBean.getObject();
                map.put(javaType, new JpaAuthHandler(javaType, repository));
            }
        }
        handlerMap = Collections.unmodifiableMap(map);
    }

    protected static class AuthJpaInvoker {
        private Class<?> userClass;
        private Repository userRepository;

    }
}
