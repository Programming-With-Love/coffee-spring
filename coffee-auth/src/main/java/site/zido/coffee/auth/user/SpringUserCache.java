package site.zido.coffee.auth.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.util.Assert;

public class SpringUserCache implements UserCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringUserCache.class);
    private final Cache cache;

    public SpringUserCache(Cache cache) {
        Assert.notNull(cache, "cache can't be null");
        this.cache = cache;
    }

    @Override
    public UserDetails getUserFromCache(String username) {
        Cache.ValueWrapper element = username != null ? cache.get(username) : null;
        LOGGER.debug("Cache hit: {}; username:{}", element != null, username);
        if (element == null) {
            return null;
        } else {
            return (UserDetails) element.get();
        }
    }

    @Override
    public void putUserInCache(UserDetails user) {
        LOGGER.debug("Cache put: {}", user.getKey());
        cache.put(user.getKey(), user);
    }

    public void removeUserFromCache(UserDetails user) {
        this.removeUserFromCache(user.getKey());
    }

    @Override
    public void removeUserFromCache(Object username) {
        LOGGER.debug("Cache remove: {}", username);
        cache.evict(username);
    }

}
