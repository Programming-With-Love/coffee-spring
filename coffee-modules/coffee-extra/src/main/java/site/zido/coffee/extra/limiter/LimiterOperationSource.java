package site.zido.coffee.extra.limiter;

import java.lang.reflect.Method;
import java.util.Collection;

public interface LimiterOperationSource {
    Collection<LimiterOperation> getLimiterOperations(Method method, Class<?> targetClass);
}
