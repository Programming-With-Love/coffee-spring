package site.zido.coffee.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.web.util.UrlPathHelper;
import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.entity.annotations.AuthEntity;
import site.zido.coffee.auth.handlers.*;
import site.zido.coffee.auth.handlers.jpa.JpaAuthHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static site.zido.coffee.auth.Constants.DEFAULT_LOGIN_URL;

/**
 * @author zido
 */
public class AuthAutoConfiguration implements BeanFactoryAware, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthAutoConfiguration.class);
    private static final String ERROR_WHEN_MULTI = String.format("多用户实体时需要使用%s标记，" +
            "并提供不同的url以帮助识别登录用户", AuthEntity.class.getName());
    private BeanFactory beanFactory;
    private AuthenticatorFactory authenticatorFactory;
    private AuthenticationFilter filter;

    public AuthAutoConfiguration(AuthenticationFilter filter) {
        this.filter = filter;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, JpaRepositoryFactoryBean> jpaRepositoryFactoryBeanMap = BeanFactoryUtils.beansOfTypeIncludingAncestors((ListableBeanFactory) beanFactory, JpaRepositoryFactoryBean.class);
        Map<String, AuthHandler<? extends IUser>> map = new HashMap<>();
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
                if (map.get(url) != null) {
                    throw new IllegalArgumentException(ERROR_WHEN_MULTI);
                }
                map.put(url, new JpaAuthHandler<>(javaType, authenticatorFactory.newChains(javaType)));
            }
        }
        if (map.isEmpty()) {
            //TODO don't register filter
            return;
        }
        map = Collections.unmodifiableMap(map);
        filter.setHandlerMap(map);
        LoginSuccessHandler successHandler = beanFactory.getBean(LoginSuccessHandler.class);
        filter.setAuthenticationSuccessHandler(successHandler);
        LoginFailureHandler loginFailureHandler = beanFactory.getBean(LoginFailureHandler.class);
        filter.setAuthenticationFailureHandler(loginFailureHandler);
        try {
            UrlPathHelper urlPathHelper = beanFactory.getBean(UrlPathHelper.class);
            filter.setUrlPathHelper(urlPathHelper);
        } catch (NoSuchBeanDefinitionException ignore) {
        }
    }
}
