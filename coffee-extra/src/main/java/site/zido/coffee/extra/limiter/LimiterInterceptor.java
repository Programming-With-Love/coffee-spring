package site.zido.coffee.extra.limiter;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author zido
 */
public class LimiterInterceptor extends AbstractLimiterInvoker implements MethodInterceptor, BeanFactoryAware, InitializingBean {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final Map<ExpressionKey, Expression> CACHE = new ConcurrentHashMap<>(3);
    private SpelExpressionParser parser = new SpelExpressionParser();
    private LimiterOperationSource limiterOperationSource;
    private FrequencyLimiter limiter;
    private StandardEvaluationContext context;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Object target = invocation.getThis();
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        Collection<LimiterOperation> operations = getLimiterOperationSource().getLimiterOperations(method, targetClass);
        if (!CollectionUtils.isEmpty(operations)) {
            for (LimiterOperation operation : operations) {
                String key = operation.getKey();
                key = generateKey(key, new AnnotatedElementKey(method, targetClass));
                long timeout = operation.getTimeout();
                try {
                    long lastTimeout = limiter.tryGet(key, timeout);
                    if (lastTimeout > 0) {
                        LOGGER.debug("限制行为{}#{} , remain: {}", targetClass.getName(), method.getName(), lastTimeout);
                        getErrorHandler().handleOnLimited(new LimiterException(key, lastTimeout, timeout));
                        return null;
                    }
                } catch (RuntimeException e) {
                    getErrorHandler().handleError(e, key);
                    return null;
                }
            }
        }
        return invocation.proceed();
    }

    private String generateKey(String expression, AnnotatedElementKey elementKey) {
        ExpressionKey expressionKey = createKey(elementKey, expression);
        Expression expr = CACHE.get(expressionKey);
        if (expr == null) {
            expr = parser.parseExpression(expression);
            CACHE.put(expressionKey, expr);
        }
        return expr.getValue(createEvaluationContext(), String.class);
    }

    private EvaluationContext createEvaluationContext() {
        return context;
    }

    private ExpressionKey createKey(AnnotatedElementKey elementKey, String expression) {
        return new ExpressionKey(elementKey, expression);
    }

    public LimiterOperationSource getLimiterOperationSource() {
        return limiterOperationSource;
    }

    public void setLimiterOperationSource(LimiterOperationSource limiterOperationSource) {
        this.limiterOperationSource = limiterOperationSource;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        context = new StandardEvaluationContext();
        context.setBeanResolver(new BeanFactoryResolver(beanFactory));
    }

    public FrequencyLimiter getLimiter() {
        return limiter;
    }

    public void setLimiter(FrequencyLimiter limiter) {
        this.limiter = limiter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.state(getLimiterOperationSource() != null, "The 'limiterOperationSources' property is required: " +
                "If there are no limiter methods, then don't use a limiter aspect.");
        Assert.state(getErrorHandler() != null, "The 'errorHandler' property is required");
        Assert.state(limiter != null, "the 'limiter' property is required");
    }

    protected static class ExpressionKey implements Comparable<ExpressionKey> {

        private final AnnotatedElementKey element;

        private final String expression;

        protected ExpressionKey(AnnotatedElementKey element, String expression) {
            this.element = element;
            this.expression = expression;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ExpressionKey)) {
                return false;
            }
            ExpressionKey otherKey = (ExpressionKey) other;
            return (this.element.equals(otherKey.element) &&
                    ObjectUtils.nullSafeEquals(this.expression, otherKey.expression));
        }

        @Override
        public int hashCode() {
            return this.element.hashCode() + (this.expression != null ? this.expression.hashCode() * 29 : 0);
        }

        @Override
        public String toString() {
            return this.element + (this.expression != null ? " with expression \"" + this.expression : "\"");
        }

        @Override
        public int compareTo(ExpressionKey other) {
            int result = this.element.toString().compareTo(other.element.toString());
            if (result == 0 && this.expression != null) {
                result = this.expression.compareTo(other.expression);
            }
            return result;
        }
    }
}
