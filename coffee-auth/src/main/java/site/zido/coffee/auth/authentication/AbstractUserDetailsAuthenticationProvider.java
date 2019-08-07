package site.zido.coffee.auth.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.Assert;
import site.zido.coffee.auth.core.CoffeeAuthMessageSource;
import site.zido.coffee.auth.user.NullUserCache;
import site.zido.coffee.auth.user.UserCache;
import site.zido.coffee.auth.core.Authentication;
import site.zido.coffee.auth.user.UserDetails;
import site.zido.coffee.auth.user.UserDetailsChecker;

import javax.naming.AuthenticationException;

/**
 * @author zido
 */
public abstract class AbstractUserDetailsAuthenticationProvider implements
        AuthenticationProvider, InitializingBean {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private UserCache userCache = new NullUserCache();
    protected boolean hideUserNotFoundExceptions = true;
    protected MessageSourceAccessor messages = CoffeeAuthMessageSource.getAccessor();
    private UserDetailsChecker preAuthenticationChecks = new DefaultPreAuthenticationChecks();
    private UserDetailsChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks();
    private boolean forcePrincipalAsString = false;

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AbstractAuthenticationException {
        String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED"
                : authentication.getName();

        boolean cacheWasUsed = true;
        UserDetails user = this.userCache.getUserFromCache(username);
        if (user == null) {
            cacheWasUsed = false;
            try {
                user = retrieveUser(username, (UsernamePasswordAuthenticationToken) authentication);
            } catch (UsernameNotFoundException e) {
                LOGGER.debug("User '{}' not found", username);
                if (hideUserNotFoundExceptions) {
                    throw new BadCredentialsException(messages.getMessage(
                            "AbstractUserDetailsAuthenticationProvider.badCredentials",
                            "Bad credentials"));
                } else {
                    throw e;
                }
            }
            Assert.notNull(user,
                    "retrieveUser returned null - a violation of the interface contract");
        }
        try {
            preAuthenticationChecks.check(user);
            additionalAuthenticationChecks(user,
                    (UsernamePasswordAuthenticationToken) authentication);
        } catch (AbstractAuthenticationException ex) {
            if (cacheWasUsed) {
                cacheWasUsed = false;
                user = retrieveUser(username,
                        (UsernamePasswordAuthenticationToken) authentication);
                preAuthenticationChecks.check(user);
                additionalAuthenticationChecks(user,
                        (UsernamePasswordAuthenticationToken) authentication);
            } else {
                throw ex;
            }
        }
        postAuthenticationChecks.check(user);

        if (!cacheWasUsed) {
            this.userCache.putUserInCache(user);
        }
        Object principalToReturn = user;

        if (forcePrincipalAsString) {
            principalToReturn = user.getUsername();
        }
        return createSuccessAuthentication(principalToReturn, authentication, user);
    }

    protected abstract UserDetails retrieveUser(String username,
                                                UsernamePasswordAuthenticationToken authentication)
            throws AbstractAuthenticationException;

    protected abstract void additionalAuthenticationChecks(UserDetails userDetails,
                                                           UsernamePasswordAuthenticationToken authentication)
            throws AbstractAuthenticationException;

    protected Authentication createSuccessAuthentication(Object principal,
                                                         Authentication authentication, UserDetails user) {
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
                principal, authentication.getCredentials(),
                user.getAuthorities());
        result.setDetails(authentication.getDetails());

        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private class DefaultPreAuthenticationChecks implements UserDetailsChecker {
        @Override
        public void check(UserDetails user) {
            if (!user.isAccountNonLocked()) {
                LOGGER.debug("User account is locked");
                throw new LockedException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.locked",
                        "User account is locked"
                ));
            }
            if (!user.enabled()) {
                LOGGER.debug("User account is disabled");

                throw new DisabledException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.disabled",
                        "User is disabled"
                ));
            }
            if (!user.isAccountNonExpired()) {
                LOGGER.debug("User account is expired");

                throw new AccountExpiredException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.expired",
                        "User account has expired"
                ));
            }
        }
    }

    private class DefaultPostAuthenticationChecks implements UserDetailsChecker {
        @Override
        public void check(UserDetails user) {
            if (!user.isCredentialsNonExpired()) {
                LOGGER.debug("User account credentials have expired");

                throw new CredentialsExpiredException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.credentialsExpired",
                        "User credentials have expired"));
            }
        }
    }
}
