package site.zido.coffee.extra.limiter;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;

/**
 * advisor
 *
 * @author zido
 */
public class BeanFactoryLimiterOperationSourceAdvisor extends AbstractBeanFactoryPointcutAdvisor {
    private LimiterOperationSource limiterOperationSource;
    private final StaticMethodMatcherPointcut pointcut = new StaticMethodMatcherPointcut() {
        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            return (getLimiterOperationSource() != null && !CollectionUtils.isEmpty(getLimiterOperationSource().getLimiterOperations(method, targetClass)));
        }
    };

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    public LimiterOperationSource getLimiterOperationSource() {
        return limiterOperationSource;
    }

    public void setLimiterOperationSource(LimiterOperationSource limiterOperationSource) {
        this.limiterOperationSource = limiterOperationSource;
    }
}
