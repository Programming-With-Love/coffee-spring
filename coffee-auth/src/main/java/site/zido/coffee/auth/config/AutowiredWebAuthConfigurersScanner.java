package site.zido.coffee.auth.config;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.Assert;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AutowiredWebAuthConfigurersScanner {
    private final ConfigurableListableBeanFactory beanFactory;

    public AutowiredWebAuthConfigurersScanner(
            ConfigurableListableBeanFactory beanFactory) {
        Assert.notNull(beanFactory, "beanFactory cannot be null");
        this.beanFactory = beanFactory;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<AuthConfigurer<Filter, FilterChainFilterBuilder>> getConfigurers() {
        List<AuthConfigurer<Filter, FilterChainFilterBuilder>> webSecurityConfigurers = new ArrayList<>();
        Map<String, AuthConfigurer> beansOfType = beanFactory
                .getBeansOfType(AuthConfigurer.class);
        for (Map.Entry<String, AuthConfigurer> entry : beansOfType.entrySet()) {
            webSecurityConfigurers.add(entry.getValue());
        }
        return webSecurityConfigurers;
    }
}
