package site.zido.coffee.core.common.utils;

import org.junit.Assert;
import org.junit.Test;
import site.zido.coffee.core.utils.maps.expire.ExpireMap;

public class ExpireMapTest {

    @Test
    public void testTTL() {
        ExpireMap<String, String> map = new ExpireMap<>();
        map.set("user", "1",60 * 1000);
        long ttl = map.ttl("user");
        Assert.assertTrue(ttl > 0);
        ttl = map.ttl("user");
        Assert.assertTrue(ttl > 0);
    }

    @Test
    public void testTTLWhenForever() {
        ExpireMap<String, String> map = new ExpireMap<>();
        map.set("user", "1");
        long ttl = map.ttl("user");
        Assert.assertEquals(ttl, -1);
        ttl = map.ttl("user");
        Assert.assertEquals(ttl, -1);
    }
}
