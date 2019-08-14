package site.zido.coffee.auth.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.Assert;
import site.zido.coffee.auth.core.CoffeeAuthMessageSource;
import site.zido.coffee.auth.user.*;
import site.zido.coffee.auth.core.Authentication;

/**
 * @author zido
 */
public abstract class AbstractUserDetailsAuthenticationProvider implements
        AuthenticationProvider, InitializingBean {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private UserCache userCache = new NullUserCache();
    protected MessageSourceAccessor messages = CoffeeAuthMessageSource.getAccessor();
    private UserChecker preAuthenticationChecks = new DefaultPreAuthenticationChecks();
    private UserChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks();
    private boolean forcePrincipalAsString = false;
    private UserDetailsReader reader = AnnotatedUserDetailsReader.getInstance();

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.userCache, "UserCache can't be null");
        Assert.notNull(this.messages, "Message source can't be null");
        Assert.notNull(this.reader, "User Reader can't be null");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AbstractAuthenticationException {
        String userKey = (authentication.getPrincipal() == null) ? "NONE_PROVIDED"
                : authentication.getName();

        boolean cacheWasUsed = true;
        UserDetails userDetails = this.userCache.getUserFromCache(userKey);
        Object principal = null;
        if (userDetails == null) {
            cacheWasUsed = false;
            principal = retrieveUser(authentication);
            userDetails = reader.parseUser(principal);
            Assert.notNull(userDetails,
                    "retrieveUser returned null - a violation of the interface contract");
        }
        try {
            preAuthenticationChecks.check(userDetails);
            additionalAuthenticationChecks(userDetails, authentication, principal);
        } catch (AbstractAuthenticationException ex) {
            if (cacheWasUsed) {
                cacheWasUsed = false;
                userDetails = reader.parseUser(retrieveUser(authentication));
                preAuthenticationChecks.check(userDetails);
                additionalAuthenticationChecks(userDetails,
                        authentication, principal);
            } else {
                throw ex;
            }
        }
        postAuthenticationChecks.check(userDetails);

        if (!cacheWasUsed) {
            this.userCache.putUserInCache(userDetails);
        }
        Object principalToReturn = userDetails;

        if (forcePrincipalAsString) {
            principalToReturn = userDetails.getKey();
        }
        return createSuccessAuthentication(principalToReturn, authentication, userDetails);
    }

    protected abstract Object retrieveUser(Authentication authentication)
            throws AbstractAuthenticationException;

    protected abstract void additionalAuthenticationChecks(UserDetails userDetails,
                                                           Authentication authentication, Object principal)
            throws AbstractAuthenticationException;

    protected Authentication createSuccessAuthentication(Object principal,
                                                         Authentication authentication, UserDetails user) {
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
                principal, authentication.getCredentials(),
                user.getAuthorities());
        result.setDetails(authentication.getDetails());

        return result;
    }

    public void setForcePrincipalAsString(boolean forcePrincipalAsString) {
        this.forcePrincipalAsString = forcePrincipalAsString;
    }

    public void setMessages(MessageSourceAccessor messages) {
        this.messages = messages;
    }

    public void setUserCache(UserCache userCache) {
        this.userCache = userCache;
    }

    public void setPreAuthenticationChecks(UserChecker preAuthenticationChecks) {
        this.preAuthenticationChecks = preAuthenticationChecks;
    }

    public void setPostAuthenticationChecks(UserChecker postAuthenticationChecks) {
        this.postAuthenticationChecks = postAuthenticationChecks;
    }

    public void setReader(UserDetailsReader reader) {
        this.reader = reader;
    }

    public UserDetailsReader getReader() {
        return reader;
    }

    private class DefaultPreAuthenticationChecks implements UserChecker {
        @Override
        public void check(UserDetails user) {
            if (!user.isAccountNonLocked()) {
                LOGGER.debug("User account is locked");
                throw new LockedException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.locked",
                        "User account is locked"
                ));
            }
            if (!user.isEnabled()) {
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

    private class DefaultPostAuthenticationChecks implements UserChecker {
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
