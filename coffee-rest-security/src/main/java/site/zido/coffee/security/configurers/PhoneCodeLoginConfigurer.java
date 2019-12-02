package site.zido.coffee.security.configurers;

import org.springframework.cache.Cache;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractRestAuthenticationFilterConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import site.zido.coffee.security.authentication.phone.PhoneAuthenticationFilter;
import site.zido.coffee.security.authentication.phone.PhoneCodeService;

/**
 * @author zido
 */
public class PhoneCodeLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractRestAuthenticationFilterConfigurer<H, PhoneCodeLoginConfigurer<H>, PhoneAuthenticationFilter> {
    private Cache cache;

    public PhoneCodeLoginConfigurer() {
        super(new PhoneAuthenticationFilter(), null);
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }

    @Override
    public void configure(H http) throws Exception {
        PhoneCodeService phoneCodeService = http.getSharedObject(PhoneCodeService.class);
        getAuthenticationFilter().setPhoneCodeService(phoneCodeService);
        super.configure(http);
    }

    public PhoneCodeLoginConfigurer<H> setPhoneParameter(String parameter) {
        getAuthenticationFilter().setPhoneParameter(parameter);
        return this;
    }

    public PhoneCodeLoginConfigurer<H> setCodeParameter(String code) {
        getAuthenticationFilter().setCodeParameter(code);
        return this;
    }

    public PhoneCodeLoginConfigurer<H> codeProcessingUrl(String url) {
        getAuthenticationFilter().setCodeRequestMatcher(createLoginProcessingUrlMatcher(url));
        return this;
    }

    public PhoneCodeLoginConfigurer<H> codeCachePrefix(String prefix) {
        getAuthenticationFilter().setCachePrefix(prefix);
        return this;
    }
}
