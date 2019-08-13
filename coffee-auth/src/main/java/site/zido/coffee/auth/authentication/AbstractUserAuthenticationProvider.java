package site.zido.coffee.auth.authentication;

import org.springframework.context.support.MessageSourceAccessor;
import site.zido.coffee.auth.core.Authentication;
import site.zido.coffee.auth.core.CoffeeAuthMessageSource;
import site.zido.coffee.auth.user.NullUserCache;
import site.zido.coffee.auth.user.UserCache;
import site.zido.coffee.auth.user.UserChecker;

public class AbstractUserAuthenticationProvider implements AuthenticationProvider {
    private UserCache userCache = new NullUserCache();
    protected boolean hideUserNotFoundExceptions = true;
    protected MessageSourceAccessor messages = CoffeeAuthMessageSource.getAccessor();
    private UserChecker preAuthenticationChecks;
    private UserChecker postAuthenticationChecks;
    private boolean forcePrincipalAsString = false;

    @Override
    public Authentication authenticate(Authentication authentication) throws AbstractAuthenticationException {
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
