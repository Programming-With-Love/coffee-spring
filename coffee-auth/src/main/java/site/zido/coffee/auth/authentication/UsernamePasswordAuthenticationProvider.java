package site.zido.coffee.auth.authentication;

import org.springframework.beans.factory.InitializingBean;
import site.zido.coffee.auth.core.Authentication;
import site.zido.coffee.auth.security.PasswordEncoder;
import site.zido.coffee.auth.user.IUserPasswordService;
import site.zido.coffee.auth.user.IUserService;
import site.zido.coffee.auth.user.UserDetails;

/**
 * @author zido
 */
public class UsernamePasswordAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider implements InitializingBean {
    private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";
    private PasswordEncoder passwordEncoder;
    private IUserService userService;
    private IUserPasswordService passwordService;
    protected boolean hideUserNotFoundExceptions = true;

    private volatile String userNotFoundEncodedPassword;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, Authentication authentication, Object principal) throws AbstractAuthenticationException {
        if (authentication.getCredentials() == null) {
            LOGGER.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
        String presentedPassword = authentication.getCredentials().toString();
//        if(!passwordEncoder.validate(presentedPassword,userDetails.getPassword))
    }

    public IUserService getUserService() {
        return userService;
    }

    private void prepareTimingAttackProtection() {
        if (this.userNotFoundEncodedPassword == null) {
            this.userNotFoundEncodedPassword = this.passwordEncoder.encode(USER_NOT_FOUND_PASSWORD);
        }
    }

    private void mitigateAgainstTimingAttack(Authentication authentication) {
        if (authentication.getCredentials() != null) {
            String presentedPassword = authentication.getCredentials().toString();
            this.passwordEncoder.validate(presentedPassword, this.userNotFoundEncodedPassword);
        }
    }

    @Override
    protected Object retrieveUser(Authentication authentication) throws AbstractAuthenticationException {
        String username = authentication.getName();
        prepareTimingAttackProtection();
        try {
            Object loadedUser = this.getUserService().findUserByUsername(username);
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
}
