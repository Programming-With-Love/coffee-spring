package site.zido.coffee.core.common.utils;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import site.zido.coffee.core.utils.DebounceExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static org.mockito.Mockito.when;

public class DebounceExecutorTest {
    @Test
    public void testDebounceRunOnce() throws Exception {
        long interval = 1000;
        Callable<Object> mock = Mockito.mock(Callable.class);
        when(mock.call()).thenReturn(null);
        DebounceExecutor executor =
                new DebounceExecutor(mock, interval);
        List<Future<Object>> list = new ArrayList<>(5);
        list.add(executor.call());
        list.add(executor.call());
        list.add(executor.call());
        list.add(executor.call());
        list.add(executor.call());
        for (Future<Object> future : list) {
            future.get();
        }
        Mockito.verify(mock, Mockito.times(1)).call();
    }

    @Test
    public void testDebounceReturnLastResult() throws Exception {
        long interval = 1000;
        Callable<Integer> mock = Mockito.mock(Callable.class);
        DebounceExecutor executor =
                new DebounceExecutor(mock, interval);
        List<Future<Integer>> list = new ArrayList<>(5);
        when(mock.call()).thenReturn(0);
        list.add(executor.call());
        when(mock.call()).thenReturn(1);
        list.add(executor.call());
        when(mock.call()).thenReturn(2);
        list.add(executor.call());
        when(mock.call()).thenReturn(3);
        list.add(executor.call());
        when(mock.call()).thenReturn(4);
        list.add(executor.call());
        for (Future<Integer> future : list) {
            Assert.assertEquals(4, future.get().intValue());
        }
        Mockito.verify(mock, Mockito.times(1)).call();
    }

    @Test
    public void testDebounceRerun() throws Exception {
        long interval = 1000;
        Callable<Integer> mock = Mockito.mock(Callable.class);
        DebounceExecutor executor =
                new DebounceExecutor(mock, interval);
        int times = 10;
        for (int i = 0; i < times; i++) {
            List<Future<Integer>> list = new ArrayList<>(5);
            when(mock.call()).thenReturn(0);
            list.add(executor.call());
            when(mock.call()).thenReturn(1);
            list.add(executor.call());
            when(mock.call()).thenReturn(2);
            list.add(executor.call());
            when(mock.call()).thenReturn(3);
            list.add(executor.call());
            if (i % 2 == 0) {
                when(mock.call()).thenReturn(4);
                list.add(executor.call());
                for (Future<Integer> future : list) {
                    Assert.assertEquals(4, future.get().intValue());
                }
            } else {
                for (Future<Integer> future : list) {
                    Assert.assertEquals(3, future.get().intValue());
                }
            }
        }
        Mockito.verify(mock, Mockito.times(times)).call();
    }
}
