package site.zido.coffee.auth.config;

import javax.servlet.Filter;


public interface AuthenticationFilterFactory {
    Filter createFilter(Class<?> userClass,
                        ObjectPostProcessor<Object> objectObjectPostProcessor);
}
