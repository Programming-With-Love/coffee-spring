package site.zido.coffee.core.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import site.zido.coffee.core.utils.maps.expire.ExpireMap;

public class ExpireMapTest {

    @RepeatedTest(20)
    public void testTTL() {
        ExpireMap<String, String> map = new ExpireMap<>();
        map.set("user", "1", 60 * 1000);
        long ttl = map.ttl("user");
        Assertions.assertTrue(ttl > 0, "返回超时时间应大于0且小于等于60000");
        Assertions.assertTrue(ttl <= 60 * 1000, "返回超时时间应小于等于60000");
        ttl = map.ttl("user");
        Assertions.assertTrue(ttl > 0, "第二次获取ttl，同样应返回超时时间应大于0且小于等于60000");
        Assertions.assertTrue(ttl <= 60 * 1000, "第二次获取ttl，同样应返回超时时间应小于等于60000");
    }

    @RepeatedTest(20)
    public void testTTLWhenForever() {
        ExpireMap<String, String> map = new ExpireMap<>();
        map.set("user", "1");
        long ttl = map.ttl("user");
        Assertions.assertEquals(-1, ttl, "返回超时时间应为-1");
        ttl = map.ttl("user");
        Assertions.assertEquals(-1, ttl, "第二次操作返回超时时间应为-1");
    }
}
