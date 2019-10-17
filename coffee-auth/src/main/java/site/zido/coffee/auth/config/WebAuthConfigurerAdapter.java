package site.zido.coffee.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebAuthConfigurerAdapter implements WebAuthConfigurer<FilterChainFilterBuilder> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private boolean defaults;

    @Override
    public void init(FilterChainFilterBuilder builder) throws Exception {

    }

    @Override
    public void configure(FilterChainFilterBuilder builder) throws Exception {

    }
}
