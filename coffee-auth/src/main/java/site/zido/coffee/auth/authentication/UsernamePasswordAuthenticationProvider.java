package site.zido.coffee.auth.authentication;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import site.zido.coffee.auth.core.Authentication;
import site.zido.coffee.auth.security.NullPasswordEncoder;
import site.zido.coffee.auth.security.PasswordEncoder;
import site.zido.coffee.auth.user.IUserPasswordService;
import site.zido.coffee.auth.user.IUserService;
import site.zido.coffee.auth.user.PasswordUser;

/**
 * @author zido
 */
public class UsernamePasswordAuthenticationProvider
        extends AbstractUserDetailsAuthenticationProvider<UsernamePasswordAuthenticationToken, PasswordUser> implements InitializingBean {
    private static PasswordEncoder NULL_PASSWORD_INSTANCE = new NullPasswordEncoder();
    private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";
    private static final String DEFAULT_USERNAME_FIELD_NAME = "username";
    private IUserService<PasswordUser> userService;
    private IUserPasswordService passwordService;
    private boolean hideUserNotFoundExceptions = true;
    private PasswordEncoder passwordEncoder = NULL_PASSWORD_INSTANCE;
    private String usernameFieldName = DEFAULT_USERNAME_FIELD_NAME;

    private volatile String userNotFoundEncodedPassword;

    public UsernamePasswordAuthenticationProvider(IUserService<PasswordUser> userService) {
        this.userService = userService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(userService, "user service can't be null");
        Assert.notNull(passwordEncoder, "passwordEncoder can't be null");
    }

    @Override
    protected PasswordUser retrieveUser(Object userKey, UsernamePasswordAuthenticationToken authentication) throws AbstractAuthenticationException {
        prepareTimingAttackProtection();
        try {
            PasswordUser loadedUser = this.getUserService().loadUser(userKey);
            if (loadedUser == null) {
                throw new InternalAuthenticationException(
                        "loaded user is null"
                );
            }
            return loadedUser;
        } catch (UsernameNotFoundException ex) {
            mitigateAgainstTimingAttack(authentication);
            if (hideUserNotFoundExceptions) {
                throw new BadCredentialsException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.badCredentials",
                        "Bad credentials"));
            }
            throw ex;
        } catch (InternalAuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationException(ex.getMessage(), ex);
        }
    }

    @Override
    protected void additionalAuthenticationChecks(PasswordUser user, Authentication authentication)
            throws AbstractAuthenticationException {
        if (authentication.getCredentials() == null) {
            LOGGER.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
        String presentedPassword = authentication.getCredentials().toString();

        if (!this.getPasswordEncoder().validate(presentedPassword, user.getPassword())) {
            LOGGER.debug("Authentication failed: password does not match stored value");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
    }

    public IUserService<PasswordUser> getUserService() {
        return userService;
    }

    private void prepareTimingAttackProtection() {
        if (this.userNotFoundEncodedPassword == null) {
            this.userNotFoundEncodedPassword = this.getPasswordEncoder().encode(USER_NOT_FOUND_PASSWORD);
        }
    }

    private void mitigateAgainstTimingAttack(Authentication authentication) {
        if (authentication.getCredentials() != null) {
            String presentedPassword = authentication.getCredentials().toString();
            this.getPasswordEncoder().validate(presentedPassword, this.userNotFoundEncodedPassword);
        }
    }

    @Override
    public boolean supports(Class<? extends Authentication> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void setHideUserNotFoundExceptions(boolean hideUserNotFoundExceptions) {
        this.hideUserNotFoundExceptions = hideUserNotFoundExceptions;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setUserService(IUserService<PasswordUser> userService) {
        this.userService = userService;
    }

    public IUserPasswordService getPasswordService() {
        return passwordService;
    }

    public void setPasswordService(IUserPasswordService passwordService) {
        this.passwordService = passwordService;
    }

    public void setUsernameFieldName(String usernameFieldName) {
        this.usernameFieldName = usernameFieldName;
    }

    public String getUsernameFieldName() {
        return usernameFieldName;
    }
}
