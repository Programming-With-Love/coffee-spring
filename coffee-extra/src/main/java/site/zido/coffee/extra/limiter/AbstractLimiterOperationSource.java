package site.zido.coffee.extra.limiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodClassKey;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注解扫描结果的缓存中心,包括类，方法
 *
 * @author zido
 */
public abstract class AbstractLimiterOperationSource implements LimiterOperationSource {
    private final static Collection<LimiterOperation> NULL_CACHING_ATTRIBUTE = Collections.emptyList();
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<Object, Collection<LimiterOperation>> attributeCache = new ConcurrentHashMap<>(16);

    @Override
    public Collection<LimiterOperation> getLimiterOperations(Method method, Class<?> targetClass) {
        if (method.getDeclaringClass() == Object.class) {
            return null;
        }
        Object limiterKey = new MethodClassKey(method, targetClass);
        Collection<LimiterOperation> cached = this.attributeCache.get(limiterKey);
        if (cached != null) {
            return (cached != NULL_CACHING_ATTRIBUTE ? cached : null);
        } else {
            Collection<LimiterOperation> cacheOps = computeLimiterOperation(method, targetClass);
            if (cacheOps != null) {
                logger.debug("Adding limiter method '{}' with attribute: {}", method.getName(), cacheOps);
                this.attributeCache.put(limiterKey, cacheOps);
            } else {
                this.attributeCache.put(limiterKey, NULL_CACHING_ATTRIBUTE);
            }
            return cacheOps;
        }
    }

    private Collection<LimiterOperation> computeLimiterOperation(Method method, Class<?> targetClass) {
        if (allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
            return null;
        }
        Method specMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        specMethod = BridgeMethodResolver.findBridgedMethod(specMethod);
        Collection<LimiterOperation> opDef = resolve(specMethod, specMethod.getDeclaringClass());
        if (opDef != null) {
            return opDef;
        }
        if (specMethod != method) {
            return resolve(method, method.getDeclaringClass());
        }
        return null;
    }

    private Collection<LimiterOperation> resolve(Method method, Class<?> clazz) {
        Collection<LimiterOperation> opDef = findLimiterOperations(method);
        if (opDef != null) {
            return opDef;
        }
        opDef = findLimiterOperations(clazz.getDeclaringClass());
        if (opDef != null && ClassUtils.isUserLevelMethod(method)) {
            return opDef;
        }
        return null;
    }

    protected abstract Collection<LimiterOperation> findLimiterOperations(Method method);

    protected abstract Collection<LimiterOperation> findLimiterOperations(Class<?> clazz);

    protected boolean allowPublicMethodsOnly() {
        return false;
    }
}
