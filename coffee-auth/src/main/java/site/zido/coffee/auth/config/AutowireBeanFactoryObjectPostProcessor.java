package site.zido.coffee.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zido
 */
public class AutowireBeanFactoryObjectPostProcessor implements ObjectPostProcessor<Object>, DisposableBean, SmartInitializingSingleton {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final AutowireCapableBeanFactory factory;
    private final List<DisposableBean> disposableBeans = new ArrayList<>();
    private final List<SmartInitializingSingleton> smartInitializingSingletons = new ArrayList<>();

    public AutowireBeanFactoryObjectPostProcessor(AutowireCapableBeanFactory factory) {
        Assert.notNull(factory, "autowireBeanFactory cannot be null");
        this.factory = factory;
    }

    @Override
    public void destroy() throws Exception {
        for (DisposableBean disposable : this.disposableBeans) {
            try {
                disposable.destroy();
            } catch (Exception error) {
                this.logger.error("destroy bean error:", error);
            }
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        for (SmartInitializingSingleton singleton : smartInitializingSingletons) {
            singleton.afterSingletonsInstantiated();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <O> O postProcess(O object) {
        if (object == null) {
            return null;
        }
        O result = null;
        try {
            result = (O) this.factory.initializeBean(object, object.toString());
        } catch (RuntimeException e) {
            Class<?> type = object.getClass();
            throw new RuntimeException(
                    "Could not postProcess " + object + " of type " + type, e);
        }
        this.factory.autowireBean(object);
        if (result instanceof DisposableBean) {
            this.disposableBeans.add((DisposableBean) result);
        }
        if (result instanceof SmartInitializingSingleton) {
            this.smartInitializingSingletons.add((SmartInitializingSingleton) result);
        }
        return result;
    }
}
