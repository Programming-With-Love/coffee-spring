package site.zido.coffee.extra.limiter;

import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 限流器注解解析器,负责解析限流器，以提供注解式限流支持
 *
 * @author zido
 * @see Limiter
 */
public class SpringLimiterAnnotationParser implements LimiterAnnotationParser {

    /**
     * 解析类上注解
     *
     * @param type target class type
     * @return operations
     */
    @Override
    public Collection<LimiterOperation> parseLimiterAnnotations(Class<?> type) {
        return parse(type);
    }

    /**
     * 解析方法上的注解
     *
     * @param method target method
     * @return operations
     */
    @Override
    public Collection<LimiterOperation> parseLimiterAnnotations(Method method) {
        return parse(method);
    }

    protected Collection<LimiterOperation> parse(AnnotatedElement ae) {
        Collection<LimiterOperation> ops = new ArrayList<>(1);

        Collection<Limiter> limiters = AnnotatedElementUtils.getAllMergedAnnotations(ae, Limiter.class);
        if (!limiters.isEmpty()) {
            for (Limiter limiter : limiters) {
                ops.add(parseLimiterAnnotation(ae, limiter));
            }
        }
        return ops;
    }

    private LimiterOperation parseLimiterAnnotation(AnnotatedElement ae, Limiter limiter) {
        LimiterOperation.Builder builder = new LimiterOperation.Builder();
        builder.setName(ae.toString());
        builder.setKey(limiter.key());
        builder.setTimeout(limiter.timeout());
        builder.setUnit(limiter.unit());
        return builder.build();
    }

    @Override
    public boolean equals(Object other) {
        return (this == other || other instanceof SpringLimiterAnnotationParser);
    }

    @Override
    public int hashCode() {
        return SpringLimiterAnnotationParser.class.hashCode();
    }
}
