package site.zido.coffee.auth.core;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * 认证相关的消息
 *
 * @author zido
 */
public class CoffeeAuthMessageSource extends ResourceBundleMessageSource {

    public CoffeeAuthMessageSource() {
        setBasename("auth.messages");
    }

    public static MessageSourceAccessor getAccessor() {
        return new MessageSourceAccessor(new CoffeeAuthMessageSource());
    }
}
