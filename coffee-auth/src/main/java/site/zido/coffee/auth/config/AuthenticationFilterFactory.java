package site.zido.coffee.auth.config;

import javax.servlet.Filter;


/**
 * @author zido
 */
public interface AuthenticationFilterFactory {
    Filter createFilter(Class<?> userClass,
                        ObjectPostProcessor<Object> objectObjectPostProcessor);
}
