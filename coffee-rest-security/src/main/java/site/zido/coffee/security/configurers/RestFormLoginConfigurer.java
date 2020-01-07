package site.zido.coffee.security.configurers;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractRestAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * @author zido
 */
public class RestFormLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractRestAuthenticationFilterConfigurer<H, RestFormLoginConfigurer<H>, UsernamePasswordAuthenticationFilter> {

    /**
     * Creates a new instance
     *
     * @see HttpSecurity#formLogin()
     */
    public RestFormLoginConfigurer() {
        super(new UsernamePasswordAuthenticationFilter(), null);
        getAuthenticationFilter().setRequiresAuthenticationRequestMatcher(createLoginProcessingUrlMatcher("/users/sessions"));
        usernameParameter("username");
        passwordParameter("password");
    }

    /**
     * The HTTP parameter to look for the username when performing authentication. Default
     * is "username".
     *
     * @param usernameParameter the HTTP parameter to look for the username when
     *                          performing authentication
     * @return the {@link FormLoginConfigurer} for additional customization
     */
    public RestFormLoginConfigurer<H> usernameParameter(String usernameParameter) {
        getAuthenticationFilter().setUsernameParameter(usernameParameter);
        return this;
    }

    /**
     * The HTTP parameter to look for the password when performing authentication. Default
     * is "password".
     *
     * @param passwordParameter the HTTP parameter to look for the password when
     *                          performing authentication
     * @return the {@link FormLoginConfigurer} for additional customization
     */
    public RestFormLoginConfigurer<H> passwordParameter(String passwordParameter) {
        getAuthenticationFilter().setPasswordParameter(passwordParameter);
        return this;
    }

    @Override
    public void init(H http) throws Exception {
        super.init(http);
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }

    /**
     * Gets the HTTP parameter that is used to submit the username.
     *
     * @return the HTTP parameter that is used to submit the username
     */
    private String getUsernameParameter() {
        return getAuthenticationFilter().getUsernameParameter();
    }

    /**
     * Gets the HTTP parameter that is used to submit the password.
     *
     * @return the HTTP parameter that is used to submit the password
     */
    private String getPasswordParameter() {
        return getAuthenticationFilter().getPasswordParameter();
    }
}
