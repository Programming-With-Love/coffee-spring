package site.zido.coffee.auth.config;

import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AuthConfigurerAdapter<O, B extends AuthBuilder<O>>
        implements AuthConfigurer<O, B> {
    private B authBuilder;

    private CompositeObjectPostProcessor objectPostProcessor = new CompositeObjectPostProcessor();

    @Override
    public void init(B builder) throws Exception {

    }

    @Override
    public void configure(B builder) throws Exception {
    }

    public B and() {
        return getBuilder();
    }

    protected final B getBuilder() {
        if (authBuilder == null) {
            throw new IllegalStateException("authBuilder cannot be null");
        }
        return authBuilder;
    }


    @SuppressWarnings("unchecked")
    protected <T> T postProcess(T object) {
        return (T) this.objectPostProcessor.postProcess(object);
    }

    public void addObjectPostProcessor(ObjectPostProcessor<?> objectPostProcessor) {
        this.objectPostProcessor.addObjectPostProcessor(objectPostProcessor);
    }

    public void setBuilder(B builder) {
        this.authBuilder = builder;
    }

    private static final class CompositeObjectPostProcessor implements
            ObjectPostProcessor<Object> {
        private List<ObjectPostProcessor<? extends Object>> postProcessors = new ArrayList<ObjectPostProcessor<?>>();

        @SuppressWarnings({"rawtypes", "unchecked"})
        public Object postProcess(Object object) {
            for (ObjectPostProcessor opp : postProcessors) {
                Class<?> oppClass = opp.getClass();
                Class<?> oppType = GenericTypeResolver.resolveTypeArgument(oppClass,
                        ObjectPostProcessor.class);
                if (oppType == null || oppType.isAssignableFrom(object.getClass())) {
                    object = opp.postProcess(object);
                }
            }
            return object;
        }

        private boolean addObjectPostProcessor(
                ObjectPostProcessor<?> objectPostProcessor) {
            boolean result = this.postProcessors.add(objectPostProcessor);
            postProcessors.sort(AnnotationAwareOrderComparator.INSTANCE);
            return result;
        }
    }
}
