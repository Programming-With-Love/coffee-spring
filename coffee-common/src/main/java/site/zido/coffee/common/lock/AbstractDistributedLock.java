package site.zido.coffee.common.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 分布式锁基类
 * <p>
 * 充分考虑意外退出情况下导致的锁被一直占用直到超时
 * <p>
 * 如果锁在spring容器中，会有spring容器进行自动释放，否则会根据jvm退出进行扫尾释放。
 * 通过{@link #unlocked}变量保证释放操作只进行一次
 *
 * @author zido
 */
public abstract class AbstractDistributedLock implements Lock, Serializable, DisposableBean {
    private static final Set<AbstractDistributedLock> CONTAINER = new HashSet<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Logger log = LoggerFactory.getLogger("distributed lock shutdown hooks");
            log.debug("try release distributed locks:number[{}]", CONTAINER.size());
            for (AbstractDistributedLock lock : CONTAINER) {
                lock.destroy();
            }
            log.debug("release distributed locks finished");
        }));
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final AtomicBoolean unlocked = new AtomicBoolean(false);

    protected AbstractDistributedLock() {
        CONTAINER.add(this);
    }

    @Override
    public void lock() {
        try {
            lockInterruptibly();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        while (!tryLock()) {
            TimeUnit.NANOSECONDS.sleep(5);
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        final long timeout = unit.toNanos(time);
        final long deadline = System.nanoTime() + timeout;
        for (; ; ) {
            if (tryLock()) {
                return true;
            }
            time = deadline - System.nanoTime();
            if (time <= 0L) {
                return false;
            }
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
    }

    @Override
    public boolean tryLock() {
        unlocked.set(false);
        return doTryLock();
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("not support condition");
    }


    @Override
    public void destroy() {
        if (unlocked.compareAndSet(false, true)) {
            logger.debug("try release:{}", getKey());
            doUnlock();
        }
    }

    @Override
    public void unlock() {
        if (unlocked.compareAndSet(false, true)) {
            doUnlock();
            CONTAINER.remove(this);
        }
    }

    protected abstract void doUnlock();

    protected abstract boolean doTryLock();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    public abstract String getKey();

}
