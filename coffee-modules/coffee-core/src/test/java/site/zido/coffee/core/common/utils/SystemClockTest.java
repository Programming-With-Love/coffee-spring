package site.zido.coffee.core.common.utils;

import org.junit.jupiter.api.Test;
import site.zido.coffee.core.utils.SystemClock;

import static org.assertj.core.api.Assertions.assertThat;

public class SystemClockTest {
    @Test
    public void testClock() throws InterruptedException {
        long now = SystemClock.now();
        Thread.sleep(20);
        long last = SystemClock.now();
        assertThat(last).isNotEqualTo(now);
    }
}
