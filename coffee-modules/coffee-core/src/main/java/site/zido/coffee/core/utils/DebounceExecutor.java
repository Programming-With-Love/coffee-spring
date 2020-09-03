package site.zido.coffee.core.utils;

import org.springframework.util.Assert;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 防抖执行器
 */
public class DebounceExecutor {
    private final ExecutorService executorService;
    private final AtomicBoolean executing = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicLong lastCall = new AtomicLong(0);
    private Callable<?> callable;
    private Runnable runnable;
    private Future<?> result;
    private final long interval;

    public DebounceExecutor(Callable<?> callable, long interval) {
        this.interval = interval;
        this.callable = callable;
        executorService = Executors.newSingleThreadExecutor();
    }

    public DebounceExecutor(Runnable runnable, long interval) {
        this.interval = interval;
        this.runnable = runnable;
        executorService = Executors.newSingleThreadExecutor();
    }

    public void run() {
        Assert.notNull(runnable, "runnable cannot be null");
        lastCall.set(System.currentTimeMillis());
        if (executing.compareAndSet(false, true)) {
            executorService.execute(() -> {
                Callable<?> tmp;
                do {
                    tmp = DebounceExecutor.this.callable;
                    long sleepTime = interval + lastCall.get() - System.currentTimeMillis();
                    if (sleepTime > 0) {
                        try {
                            //noinspection BusyWait
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            throw new IllegalStateException(e);
                        }
                    } else {
                        break;
                    }
                } while (tmp != callable);
                running.set(true);
                try {
                    this.runnable.run();
                } finally {
                    running.set(false);
                    executing.set(false);
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    public <R> Future<R> call() {
        Assert.notNull(callable, "callable cannot be null");
        lastCall.set(System.currentTimeMillis());
        if (executing.compareAndSet(false, true)) {
            result = executorService.submit(() -> {
                Callable<?> tmp;
                do {
                    tmp = DebounceExecutor.this.callable;
                    long sleepTime = interval + lastCall.get() - System.currentTimeMillis();
                    if (sleepTime > 0) {
                        //noinspection BusyWait
                        Thread.sleep(sleepTime);
                    } else {
                        break;
                    }
                } while (tmp != callable);
                running.set(true);
                try {
                    return this.callable.call();
                } catch (Exception e) {
                    throw new ExecutionException(e);
                } finally {
                    running.set(false);
                    executing.set(false);
                }
            });
        }
        return (Future<R>) result;
    }
}
