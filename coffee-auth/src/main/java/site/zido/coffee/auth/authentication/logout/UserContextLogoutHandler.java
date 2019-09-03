package site.zido.coffee.auth.authentication.logout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.zido.coffee.auth.context.UserContext;
import site.zido.coffee.auth.context.UserHolder;
import site.zido.coffee.auth.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UserContextLogoutHandler implements LogoutHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private boolean invalidateHttpSession = true;
    private boolean clearAuthentication = true;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (invalidateHttpSession) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                logger.debug("Invalidating session:{}", session.getId());
                session.invalidate();
            }
        }
        if (clearAuthentication) {
            UserContext userContext = UserHolder.get();
            userContext.setAuthentication(null);
        }
        UserHolder.clearContext();
    }

    public void setInvalidateHttpSession(boolean invalidateHttpSession) {
        this.invalidateHttpSession = invalidateHttpSession;
    }

    public void setClearAuthentication(boolean clearAuthentication) {
        this.clearAuthentication = clearAuthentication;
    }

    public boolean isInvalidateHttpSession() {
        return invalidateHttpSession;
    }

    public boolean isClearAuthentication() {
        return clearAuthentication;
    }
}
