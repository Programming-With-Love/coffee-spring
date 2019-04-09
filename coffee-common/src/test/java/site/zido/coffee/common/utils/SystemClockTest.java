package site.zido.coffee.common.utils;

import org.junit.Assert;
import org.junit.Test;

public class SystemClockTest {
    @Test
    public void testClock() throws InterruptedException {
        long now = SystemClock.now();
        Thread.sleep(2);
        long last = SystemClock.now();
        Assert.assertNotEquals(now,last);
    }
}
