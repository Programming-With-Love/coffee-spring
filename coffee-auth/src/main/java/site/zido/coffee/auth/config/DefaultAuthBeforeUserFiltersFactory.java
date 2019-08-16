package site.zido.coffee.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import site.zido.coffee.auth.context.AuthContextPersistenceFilter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 * 认证前的过滤器
 *
 * @author zido
 */
public class DefaultAuthBeforeUserFiltersFactory implements UserFiltersFactory {
    private AuthContextPersistenceFilter authContextPersistenceFilter;

    @Override
    public List<Filter> create(Class<?> userClass) {
        List<Filter> filters = new ArrayList<>();
        filters.add(authContextPersistenceFilter);
        return filters;
    }

    @Autowired(required = false)
    public void AuthContextPersistenceFilter(AuthContextPersistenceFilter filter) {
        this.authContextPersistenceFilter = filter;
    }
}
