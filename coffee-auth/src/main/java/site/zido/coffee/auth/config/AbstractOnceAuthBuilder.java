package site.zido.coffee.auth.config;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 基础建造者
 *
 * @author zido
 */
public abstract class AbstractOnceAuthBuilder<O> implements AuthBuilder<O> {
    private AtomicBoolean building = new AtomicBoolean();
    private O result;

    @Override
    public final O build() throws Exception {
        if (this.building.compareAndSet(false, true)) {
            this.result = doBuild();
            return this.result;
        }
        return result;
    }

    protected abstract O doBuild() throws Exception;
}
