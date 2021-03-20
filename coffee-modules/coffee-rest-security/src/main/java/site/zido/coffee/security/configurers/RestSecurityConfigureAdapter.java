package site.zido.coffee.security.configurers;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import site.zido.coffee.security.authentication.RestAuthenticationFailureHandler;
import site.zido.coffee.security.authentication.RestAuthenticationSuccessHandler;
import site.zido.coffee.security.token.RestAuthenticationEntryPoint;

/**
 * 包装WebSecurityConfigurerAdapter,加入关注restful api的默认配置
 */
public class RestSecurityConfigureAdapter extends WebSecurityConfigurerAdapter {
    private boolean disableDefaults = false;

    public RestSecurityConfigureAdapter() {
        super(true);
    }

    protected void configure(HttpSecurity http) throws Exception {
        if (!disableDefaults) {
            http.csrf(AbstractHttpConfigurer::disable).addFilter(new WebAsyncManagerIntegrationFilter())
                    .exceptionHandling(handling -> handling.accessDeniedHandler(new RestAccessDeniedHandlerImpl())
                            .authenticationEntryPoint(new RestAuthenticationEntryPoint()))
                    .headers().and().apply(new RestSecurityContextConfigurer<>()).and()
                    .formLogin(form -> form.successHandler(new RestAuthenticationSuccessHandler())
                            .failureHandler(new RestAuthenticationFailureHandler())
                            .loginProcessingUrl("/users/sessions"))
                    .anonymous().and().servletApi().and().logout();
        }
    }
}
