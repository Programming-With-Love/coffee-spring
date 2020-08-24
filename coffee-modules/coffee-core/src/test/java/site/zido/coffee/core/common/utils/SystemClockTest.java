package site.zido.coffee.core.common.utils;

import org.junit.Assert;
import org.junit.Test;
import site.zido.coffee.core.utils.SystemClock;

public class SystemClockTest {
    @Test
    public void testClock() throws InterruptedException {
        long now = SystemClock.now();
        Thread.sleep(2);
        long last = SystemClock.now();
        Assert.assertNotEquals(now,last);
    }
}
