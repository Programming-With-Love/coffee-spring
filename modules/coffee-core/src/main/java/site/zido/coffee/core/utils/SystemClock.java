package site.zido.coffee.core.utils;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 内存缓存的高并发适用钟摆
 *
 * @author zido
 */
public class SystemClock {
    private final long period;
    private final AtomicLong now;

    private SystemClock(long period) {
        this.period = period;
        this.now = new AtomicLong(System.currentTimeMillis());
        scheduleClockUpdating();
    }

    private static SystemClock instance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * Now time millis.
     *
     * @return the long
     */
    public static long now() {
        return instance().currentTimeMillis();
    }

    private void scheduleClockUpdating() {

        ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1,
                r -> {
                    Thread thread = new Thread(r, "System Clock");
                    thread.setDaemon(true);
                    return thread;
                });
        scheduler.scheduleAtFixedRate(() -> now.set(System.currentTimeMillis()), period, period, TimeUnit.MILLISECONDS);
    }

    private long currentTimeMillis() {
        return now.get();
    }

    private static class InstanceHolder {
        /**
         * The Instance.
         */
        static final SystemClock INSTANCE = new SystemClock(1);
    }

}
