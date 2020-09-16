package site.zido.coffee.security.configurers;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import site.zido.coffee.security.authentication.RestAuthenticationFailureHandler;
import site.zido.coffee.security.authentication.RestAuthenticationSuccessHandler;

/**
 * 包装WebSecurityConfigurerAdapter,加入关注restful api的默认配置
 */
public class DefaultRestSecurityConfigureAdapter extends WebSecurityConfigurerAdapter {
    private boolean disableDefaults = false;

    public DefaultRestSecurityConfigureAdapter(boolean disableDefaults) {
        super(true);
        this.disableDefaults = disableDefaults;
    }

    public DefaultRestSecurityConfigureAdapter(){
        super(true);
    }

    protected void configure(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .addFilter(new WebAsyncManagerIntegrationFilter())
                .exceptionHandling(handling ->
                        handling.accessDeniedHandler(new RestAccessDeniedHandlerImpl())
                )
                .headers().and()
                .apply(new RestSecurityContextConfigurer<>()).and()
                .formLogin(form ->
                        form.successHandler(new RestAuthenticationSuccessHandler())
                                .failureHandler(new RestAuthenticationFailureHandler())
                )
                .anonymous().and()
                .servletApi().and()
                .logout();
    }
}
