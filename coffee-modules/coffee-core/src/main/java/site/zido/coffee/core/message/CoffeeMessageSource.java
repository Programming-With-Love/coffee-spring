package site.zido.coffee.core.message;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

public class CoffeeMessageSource extends ResourceBundleMessageSource {
    public CoffeeMessageSource() {
        setBasename("spring.coffee.messages");
    }

    public static MessageSourceAccessor getAccessor() {
        return new MessageSourceAccessor(new CoffeeMessageSource());
    }
}
