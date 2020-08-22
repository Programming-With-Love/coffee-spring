package site.zido.coffee.extra.lock;

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
    private static final long serialVersionUID = -4560451450005316185L;
    private static final Set<AbstractDistributedLock> CONTAINER = new HashSet<>();
    private static final Logger log = LoggerFactory.getLogger("distributed lock manager");


    static {
        //收到结束信号进行扫尾回收
        Runtime.getRuntime().addShutdownHook(new Thread(AbstractDistributedLock::releaseAll));
    }

    public static void releaseAll(){
        log.debug("try release distributed locks:number[{}]", CONTAINER.size());
        for (AbstractDistributedLock lock : CONTAINER) {
            lock.destroy();
        }
        log.debug("release distributed locks finished");
    }

    private final boolean isSpringBean;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final AtomicBoolean unlocked = new AtomicBoolean(false);

    /**
     * 默认认为此分布式锁生命周期由spring进行管理，非正常关闭导致的未解锁会由spring负责扫尾解锁工作
     */
    protected AbstractDistributedLock() {
        this(true);
    }

    /**
     * 如果是非spring bean创建的情况，非正常关闭导致的未解锁会由jvm退出信号负责扫尾解锁工作
     *
     * @param isSpringBean 是否是spring的bean
     */
    protected AbstractDistributedLock(boolean isSpringBean) {
        this.isSpringBean = isSpringBean;
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
        boolean result = doTryLock();
        //保证只有当前拿到锁的线程能够修改unlock
        if (result) {
            unlocked.set(false);
            if (!isSpringBean) {
                CONTAINER.add(this);
            }
        }
        return result;
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
            if (!isSpringBean) {
                CONTAINER.remove(this);
            }
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
