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
    private final Map<Class<?>, Object> sharedObjects = new HashMap<>();

    private final boolean allowConfigurersOfSameType;

    private ObjectPostProcessor<Object> objectPostProcessor;

    private BuildState buildState = BuildState.UNBUILT;

    protected AbstractConfiguredAuthBuilder(ObjectPostProcessor<Object> objectPostProcessor) {
        this(objectPostProcessor, false);
    }

    protected AbstractConfiguredAuthBuilder(ObjectPostProcessor<Object> objectPostProcessor,
                                            boolean allowConfigurersOfSameType) {
        Assert.notNull(objectPostProcessor, "objectPostProcessor cannot be null");
        this.objectPostProcessor = objectPostProcessor;
        this.allowConfigurersOfSameType = allowConfigurersOfSameType;
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
            List<AuthConfigurer<O, B>> configs = allowConfigurersOfSameType
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

    public <C extends AuthConfigurer<O, B>> List<C> getConfigurers(Class<C> clazz) {
        List<C> configs = (List<C>) this.configurers.get(clazz);
        if (configs == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(configs);
    }

    public <C extends AuthConfigurer<O, B>> C getConfigurer(Class<C> clazz) {
        List<AuthConfigurer<O, B>> configs = this.configurers.get(clazz);
        if (configs == null) {
            return null;
        }
        if (configs.size() != 1) {
            throw new IllegalStateException("Only one configurer expected for type "
                    + clazz + ", but got " + configs);
        }
        return (C) configs.get(0);
    }

    public <C extends AuthConfigurer<O, B>> C removeConfigurer(Class<C> clazz) {
        List<AuthConfigurer<O, B>> configs = this.configurers.remove(clazz);
        if (configs == null) {
            return null;
        }
        if (configs.size() != 1) {
            throw new IllegalStateException("Only one configurer expected for type "
                    + clazz + ", but got " + configs);
        }
        return (C) configs.get(0);
    }

    public O objectPostProcessor(ObjectPostProcessor<Object> objectPostProcessor) {
        Assert.notNull(objectPostProcessor, "objectPostProcessor cannot be null");
        this.objectPostProcessor = objectPostProcessor;
        return (O) this;
    }

    protected <P> P postProcess(P object) {
        return this.objectPostProcessor.postProcess(object);
    }

    protected final O doBuild() throws Exception {
        synchronized (configurers) {
            buildState = BuildState.INITIALIZING;
            beforeInit();
            init();

            buildState = BuildState.CONFIGURING;

            beforeConfigure();
            configure();

            buildState = BuildState.BUILDING;
            O result = performBuild();

            buildState = BuildState.BUILT;
            return result;
        }
    }

    protected abstract O performBuild() throws Exception;

    private void configure() throws Exception {
        Collection<AuthConfigurer<O, B>> configurers = getConfigurers();
        for (AuthConfigurer<O, B> configurer : configurers) {
            configurer.configure((B) this);
        }
    }

    protected void beforeConfigure() {

    }

    private void init() throws Exception {
        Collection<AuthConfigurer<O, B>> configurers = getConfigurers();
        for (AuthConfigurer<O, B> configurer : configurers) {
            configurer.init((B) this);
        }
        for (AuthConfigurer<O, B> configurer : configurersAddedInInitializing) {
            configurer.init((B) this);
        }
    }

    private Collection<AuthConfigurer<O, B>> getConfigurers() {
        List<AuthConfigurer<O, B>> result = new ArrayList<AuthConfigurer<O, B>>();
        for (List<AuthConfigurer<O, B>> configs : this.configurers.values()) {
            result.addAll(configs);
        }
        return result;
    }

    protected void beforeInit() {

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
