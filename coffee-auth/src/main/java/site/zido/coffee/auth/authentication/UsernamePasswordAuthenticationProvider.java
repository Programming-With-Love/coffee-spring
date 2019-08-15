package site.zido.coffee.auth.authentication;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import site.zido.coffee.auth.core.Authentication;
import site.zido.coffee.auth.security.NullPasswordEncoder;
import site.zido.coffee.auth.security.PasswordEncoder;
import site.zido.coffee.auth.user.IUserPasswordService;
import site.zido.coffee.auth.user.IUserService;
import site.zido.coffee.auth.user.UserDetails;
import site.zido.coffee.auth.user.UsernamePasswordUser;

/**
 * @author zido
 */
public class UsernamePasswordAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider implements InitializingBean {
    private static PasswordEncoder NULL_PASSWORD_INSTANCE = new NullPasswordEncoder();
    private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";
    private static final String DEFAULT_PASSWORD_FIELD_NAME = "password";
    private IUserService userService;
    private IUserPasswordService passwordService;
    private boolean hideUserNotFoundExceptions = true;
    private PasswordEncoder passwordEncoder = NULL_PASSWORD_INSTANCE;
    private Class<?> userClass;
    private String passwordFieldName = DEFAULT_PASSWORD_FIELD_NAME;

    private volatile String userNotFoundEncodedPassword;

    public UsernamePasswordAuthenticationProvider(Class<?> userClass) {
        this.userClass = userClass;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(userClass, "the user class can't be null");
        Assert.notNull(userService, "user service can't be null");
        Assert.notNull(passwordEncoder, "passwordEncoder can't be null");
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, Authentication authentication, Object principal) throws AbstractAuthenticationException {
        UsernamePasswordUser user = (UsernamePasswordUser) principal;
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

    public IUserService getUserService() {
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
    protected Object retrieveUser(Authentication authentication) throws AbstractAuthenticationException {
        String username = authentication.getName();
        prepareTimingAttackProtection();
        try {
            Object loadedUser = this.getUserService().loadUser(username, passwordFieldName, userClass);
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
    public boolean supports(Class<?> authentication) {
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

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    public IUserPasswordService getPasswordService() {
        return passwordService;
    }

    public void setPasswordService(IUserPasswordService passwordService) {
        this.passwordService = passwordService;
    }

    public void setPasswordFieldName(String passwordFieldName) {
        this.passwordFieldName = passwordFieldName;
    }

    public String getPasswordFieldName() {
        return passwordFieldName;
    }
}
