package site.zido.coffee.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.*;

public abstract class AbstractConfiguredAuthBuilder<O, B extends AuthBuilder<O>>
        extends AbstractOnceAuthBuilder<O> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LinkedHashMap<Class<? extends AuthConfigurer<O, B>>, List<AuthConfigurer<O, B>>> configurers =
            new LinkedHashMap<>();

    private final List<AuthConfigurer<O, B>> configurersAddedInInitializing =
            new ArrayList<>();
    private final Map<Class<? extends Object>, Object> sharedObjects = new HashMap<>();

    private final boolean allowConfirersOfSameType;

    private ObjectPostProcessor<Object> objectPostProcessor;

    private BuildState buildState = BuildState.UNBUILT;

    protected AbstractConfiguredAuthBuilder(ObjectPostProcessor<Object> objectPostProcessor) {
        this(objectPostProcessor, false);
    }

    protected AbstractConfiguredAuthBuilder(ObjectPostProcessor<Object> objectPostProcessor,
                                            boolean allowConfirersOfSameType) {
        Assert.notNull(objectPostProcessor, "objectPostProcessor cannot be null");
        this.objectPostProcessor = objectPostProcessor;
        this.allowConfirersOfSameType = allowConfirersOfSameType;
    }

    public <C extends AuthConfigurerAdapter<O, B>> C apply(C configurer)
            throws Exception {
        configurer.addObjectPostProcessor(objectPostProcessor);
        configurer.setBuilder((B) this);
        add(configurer);
        return configurer;
    }

    public <C extends AuthConfigurer<O, B>> C apply(C configurer) throws Exception {
        add(configurer);
        return configurer;
    }

    private <C extends AuthConfigurer<O, B>> void add(C configurer) throws Exception {
        Assert.notNull(configurer, "configurer cannot be null");
        Class<? extends AuthConfigurer<O, B>> clazz =
                (Class<? extends AuthConfigurer<O, B>>) configurer.getClass();
        synchronized (configurer) {
            if (buildState.isConfigured()) {
                throw new IllegalStateException("Cannot apply " + configurer
                        + " to already built object");
            }
            List<AuthConfigurer<O, B>> configs = allowConfirersOfSameType
                    ? this.configurers.get(clazz) : null;
            if (configs == null) {
                configs = new ArrayList<>(1);
            }
            configs.add(configurer);
            this.configurers.put(clazz, configs);
            if (buildState.isInitializing()) {
                this.configurersAddedInInitializing.add(configurer);
            }
        }
    }

    private enum BuildState {
        UNBUILT(0),

        INITIALIZING(1),

        CONFIGURING(2),

        BUILDING(3),

        BUILT(4);

        private final int order;

        BuildState(int order) {
            this.order = order;
        }

        public boolean isInitializing() {
            return INITIALIZING.order == order;
        }

        public boolean isConfigured() {
            return order >= CONFIGURING.order;
        }
    }

}
