package site.zido.coffee.extra.security;

import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 可以直接注入使用
 * <p>
 * 注意不同的业务（例如对接网易与对接其他第三方接口），
 * 在注入bean的时候，记得使用命名bean或者可以直接继承实现，注入下一层的bean
 *
 * @author zido
 */
public class StringRedisTemplateSecurity extends AbstractSecurity {

    private String nonceKey;
    private StringRedisTemplate template;

    public StringRedisTemplateSecurity(String token, String nonceKey, StringRedisTemplate template) {
        super(token);
        this.nonceKey = nonceKey;
        this.template = template;
    }

    @Override
    protected void clearNonce() {
        template.delete(nonceKey);
    }

    @Override
    protected boolean addNonce(String nonce) {
        return 1 == template.opsForSet().add(nonceKey, nonce);
    }
}
