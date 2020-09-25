package limiter;

import org.junit.Assert;
import org.junit.Test;
import site.zido.coffee.extra.limiter.MemoryFrequencyLimiter;

public class MemoryFrequencyLimiterTest {
    @Test
    public void testWhenTimeoutIsEqual() throws InterruptedException {
        MemoryFrequencyLimiter limiter = new MemoryFrequencyLimiter();
        long test = limiter.tryGet("test", 1);
        Assert.assertEquals(0L, test);
        long last = limiter.tryGet("test", 1);
        Assert.assertTrue(last > 0);
        Thread.sleep(1000);
        long result = limiter.tryGet("test", 1);
        Assert.assertEquals(0, result);
        last = limiter.tryGet("test", 1);
        Assert.assertTrue(last > 0);
    }

    @Test
    public void testWhenTimeoutIsNotEqual() throws InterruptedException {
        MemoryFrequencyLimiter limiter = new MemoryFrequencyLimiter();
        long test = limiter.tryGet("test", 1);
        Assert.assertEquals(0L, test);
        long last = limiter.tryGet("test", 2);
        Assert.assertTrue(last > 0);
        Thread.sleep(2000);
        long result = limiter.tryGet("test", 2);
        Assert.assertEquals(0, result);
        last = limiter.tryGet("test", 1);
        Assert.assertTrue(last > 0);
    }
}
