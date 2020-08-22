package site.zido.coffee.extra.limiter;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * 注解解析器
 *
 * @author zido
 */
public interface LimiterAnnotationParser {
    Collection<LimiterOperation> parseLimiterAnnotations(Class<?> type);

    Collection<LimiterOperation> parseLimiterAnnotations(Method method);
}
