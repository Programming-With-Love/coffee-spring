package limiter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import site.zido.coffee.core.utils.maps.expire.ExpireMap;
import site.zido.coffee.extra.limiter.MemoryFrequencyLimiter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MemoryFrequencyLimiterTest {
    @Test
    public void testWhenTimeoutIsEqual() throws InterruptedException {
        @SuppressWarnings("unchecked") ExpireMap<String, Object> expireMap = mock(ExpireMap.class);
        MemoryFrequencyLimiter limiter = new MemoryFrequencyLimiter();
        limiter.setExpireMap(expireMap);
        //当第一次设置必定成功
        when(expireMap.setNx(eq("test"), any(), eq(1L))).thenReturn(true);
        long test = limiter.tryGet("test", 1);
        verify(expireMap, never()).ttl(any());
        Assertions.assertEquals(0L, test);
        //第二次设置时，因key已存在，则必定失败
        when(expireMap.setNx(eq("test"), any(), eq(1L))).thenReturn(false);
        when(expireMap.ttl("test")).thenReturn(1L);
        long last = limiter.tryGet("test", 1);
        Assertions.assertEquals(1L, last);
    }

    @Test
    public void testWhenTimeoutIsNotEqual() throws InterruptedException {
        @SuppressWarnings("unchecked") ExpireMap<String, Object> expireMap = mock(ExpireMap.class);
        MemoryFrequencyLimiter limiter = new MemoryFrequencyLimiter();
        limiter.setExpireMap(expireMap);
        when(expireMap.setNx(eq("test"), any(), eq(1L))).thenReturn(true);
        long test = limiter.tryGet("test", 1);
        verify(expireMap, never()).ttl(any());
        Assertions.assertEquals(0L, test);
        when(expireMap.setNx(eq("test"), any(), eq(2L))).thenReturn(false);
        when(expireMap.ttl("test")).thenReturn(1L);
        long last = limiter.tryGet("test", 2);
        Assertions.assertEquals(1, last);
    }
}
