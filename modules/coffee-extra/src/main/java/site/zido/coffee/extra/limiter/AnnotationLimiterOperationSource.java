package site.zido.coffee.extra.limiter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 注解扫描执行
 *
 * @author zido
 */
public class AnnotationLimiterOperationSource extends AbstractLimiterOperationSource {
    private final Set<LimiterAnnotationParser> annotationParsers;
    private final boolean publicMethodsOnly;

    public AnnotationLimiterOperationSource() {
        this(true);
    }

    public AnnotationLimiterOperationSource(boolean publicMethodsOnly) {
        this.publicMethodsOnly = publicMethodsOnly;
        this.annotationParsers = new LinkedHashSet<>(1);
        this.annotationParsers.add(new SpringLimiterAnnotationParser());
    }

    @Override
    protected Collection<LimiterOperation> findLimiterOperations(Method method) {
        return determine(parser -> parser.parseLimiterAnnotations(method));
    }

    @Override
    protected Collection<LimiterOperation> findLimiterOperations(Class<?> clazz) {
        return determine(parser -> parser.parseLimiterAnnotations(clazz));
    }

    protected Collection<LimiterOperation> determine(LimiterOperationProvider provider) {
        ArrayList<LimiterOperation> ops = null;
        for (LimiterAnnotationParser parser : annotationParsers) {
            Collection<LimiterOperation> operations = provider.getLimiterOperations(parser);
            if (operations != null) {
                if (ops == null) {
                    ops = new ArrayList<>();
                }
                ops.addAll(operations);
            }
        }
        return ops;
    }

    @Override
    protected boolean allowPublicMethodsOnly() {
        return this.publicMethodsOnly;
    }

    protected interface LimiterOperationProvider {

        /**
         * Return the {@link LimiterOperation} instance(s) provided by the specified parser.
         *
         * @param parser the parser to use
         * @return the cache operations, or {@code null} if none found
         */
        Collection<LimiterOperation> getLimiterOperations(LimiterAnnotationParser parser);
    }
}
