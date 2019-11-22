package site.zido.coffee.security.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.util.Assert;

import java.util.Random;

/**
 * 使用spring cache 实现的手机号验证码管理器
 *
 * @author zido
 */
public abstract class AbstractSpringCacheMobileCodeManager implements MobileCodeManager {
    private static Logger LOGGER = LoggerFactory.getLogger(AbstractSpringCacheMobileCodeManager.class);
    private int codeLength = 6;
    private Cache cache;
    private String prefix = "";

    public AbstractSpringCacheMobileCodeManager(Cache cache) {
        Assert.notNull(cache, "cache manager cannot be null");
        this.cache = cache;
    }

    @Override
    public void sendCode(String mobile) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < codeLength; i++) {
            builder.append(random.nextInt(10));
        }
        String code = builder.toString();
        cache.put(prefix + mobile, code);
        LOGGER.debug("send send code:{} to mobile:{}", code, mobile);
    }

    /**
     * 实际调用发送验证码
     *
     * @param mobile 手机号
     */
    protected abstract void doSendCode(String mobile);

    @Override
    public boolean validateCode(String mobile, String code) {
        Cache.ValueWrapper wrapper = cache.get(prefix + mobile);
        if (wrapper == null) {
            return false;
        }
        return code.equals(wrapper.get());
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setCodeLength(int codeLength) {
        this.codeLength = codeLength;
    }
}
