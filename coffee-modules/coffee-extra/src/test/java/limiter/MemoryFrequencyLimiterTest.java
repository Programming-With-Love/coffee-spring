package limiter;

import org.junit.Assert;
import org.junit.Test;
import site.zido.coffee.extra.limiter.MemoryFrequencyLimiter;

public class MemoryFrequencyLimiterTest {
    @Test
    public void testWhenTimeoutIsEqual() throws InterruptedException {
        MemoryFrequencyLimiter limiter = new MemoryFrequencyLimiter();
        long test = limiter.tryGet("test", 5);
        Assert.assertEquals(0L, test);
        long last = limiter.tryGet("test", 5);
        Assert.assertTrue(last > 0);
        Thread.sleep(5000);
        long result = limiter.tryGet("test", 5);
        Assert.assertEquals(0, result);
    }

    @Test
    public void testWhenTimeoutIsNotEqual() throws InterruptedException {
        MemoryFrequencyLimiter limiter = new MemoryFrequencyLimiter();
        long test = limiter.tryGet("test", 3);
        Assert.assertEquals(0L, test);
        long last = limiter.tryGet("test", 5);
        Assert.assertTrue(last > 0);
        Thread.sleep(5000);
        long result = limiter.tryGet("test", 6);
        Assert.assertEquals(0, result);
    }
}
