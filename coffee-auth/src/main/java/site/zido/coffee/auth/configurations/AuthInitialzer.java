package site.zido.coffee.auth.configurations;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.entity.annotations.AuthEntity;
import site.zido.coffee.auth.handlers.AuthHandler;
import site.zido.coffee.auth.handlers.jpa.JpaAuthHandler;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static site.zido.coffee.auth.Constants.DEFAULT_LOGIN_URL;

/**
 * @author zido
 */
public class AuthInitialzer implements BeanFactoryAware, InitializingBean {
    private static final String ERROR_WHEN_MULTI = String.format("多用户实体时需要使用%s标记，" +
            "并提供不同的url以帮助识别登录用户", AuthEntity.class.getName());
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() throws Exception {
        Map<String, JpaRepositoryFactoryBean> jpaRepositoryFactoryBeanMap = BeanFactoryUtils.beansOfTypeIncludingAncestors((ListableBeanFactory) beanFactory, JpaRepositoryFactoryBean.class);
        Map<String, AuthHandler<? extends IUser, ? extends Serializable>> map = new HashMap<>();
        for (JpaRepositoryFactoryBean factoryBean : jpaRepositoryFactoryBeanMap.values()) {
            Class<?> javaType = factoryBean.getEntityInformation().getJavaType();
            if (javaType.isAssignableFrom(IUser.class)) {
                JpaRepository repository = (JpaRepository) factoryBean.getObject();
                AuthEntity annotation = AnnotationUtils.getAnnotation(javaType, AuthEntity.class);
                String url;
                if (annotation != null) {
                    url = annotation.url().trim();
                    if (!url.startsWith("/")) {
                        url = "/" + url;
                    }
                } else {
                    url = DEFAULT_LOGIN_URL;
                }
                AuthHandler oldValue = map.put(url, new JpaAuthHandler<>(javaType, repository));
                if (oldValue != null) {
                    throw new IllegalArgumentException(ERROR_WHEN_MULTI);
                }
            }
        }
        map = Collections.unmodifiableMap(map);
    }
}
