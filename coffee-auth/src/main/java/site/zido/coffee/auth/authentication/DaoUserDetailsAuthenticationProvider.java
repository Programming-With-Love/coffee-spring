package site.zido.coffee.auth.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.zido.coffee.auth.security.PasswordEncoder;
import site.zido.coffee.auth.user.IUserPasswordService;
import site.zido.coffee.auth.user.IUserService;
import site.zido.coffee.auth.user.UserDetails;

/**
 * @author zido
 */
public class DaoUserDetailsAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";
    private PasswordEncoder passwordEncoder;
    private IUserService userService;
    private IUserPasswordService passwordService;

    private volatile String userNotFoundEncodedPassword;

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AbstractAuthenticationException {
        prepareTimingAttackProtection();
        try {
            UserDetails loadedUser = this.getUserService().findUserByUsername(username);
            if (loadedUser == null) {
                throw new InternalAuthenticationException(
                        "loaded user is null"
                );
            }
            return loadedUser;
        } catch (UsernameNotFoundException ex) {
            mitigateAgainstTimingAttack(authentication);
            throw ex;
        } catch (InternalAuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationException(ex.getMessage(), ex);
        }
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AbstractAuthenticationException {
        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");

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

    private void mitigateAgainstTimingAttack(UsernamePasswordAuthenticationToken authentication) {
        if (authentication.getCredentials() != null) {
            String presentedPassword = authentication.getCredentials().toString();
            this.passwordEncoder.validate(presentedPassword, this.userNotFoundEncodedPassword);
        }
    }
}
