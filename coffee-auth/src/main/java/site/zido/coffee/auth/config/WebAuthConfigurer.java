package site.zido.coffee.auth.config;

import javax.servlet.Filter;

/**
 * @author zido
 */
public interface WebAuthConfigurer<T extends AuthBuilder<Filter>> extends AuthConfigurer<Filter, T> {
}
