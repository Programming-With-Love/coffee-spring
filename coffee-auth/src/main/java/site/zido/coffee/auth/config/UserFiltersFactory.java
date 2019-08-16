package site.zido.coffee.auth.config;

import javax.servlet.Filter;
import java.util.List;

/**
 * 构造用户相关的过滤器链
 *
 * @author zido
 */
public interface UserFiltersFactory {
    List<Filter> create(Class<?> userClass);
}
