package site.zido.coffee.auth.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.zido.coffee.auth.authentication.AnonymousAuthenticationToken;
import site.zido.coffee.auth.authentication.AuthenticationTokenFactory;
import site.zido.coffee.auth.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author zido
 */
public class HttpSessionUserContextRepository implements UserContextRepository {
    public static final String USER_CONTEXT_KEY = "USER_CONTEXT";
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Object contextObject = UserHolder.createEmptyContext();
    private String userContextKey = USER_CONTEXT_KEY;

    @Override
    public UserContext loadContext(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        UserContext context = readSecurityContextFromSession(session);
        if (context == null) {
            logger.debug("No user context aws available from the http session:{}. A new one will be created.", session);
            context = generateNewContext();
        }
        return context;
    }

    @Override
    public void saveContext(UserContext context, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = context.getAuthentication();
        HttpSession session = request.getSession(false);
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            logger.debug("user context is empty or contents are anonymous");
            if (session != null) {
                session.removeAttribute(userContextKey);
            }
            return;
        }
        if (session == null) {
            if (!contextObject.equals(context)) {
                try {
                    session = request.getSession(true);
                } catch (IllegalStateException e) {
                    logger.warn("Failed to create a session", e);
                }
            }
        }
        if (session != null) {
            session.setAttribute(userContextKey, context);
            logger.debug("user Context {} stored to HttpSession: {}", context, session);
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return false;
        }

        return session.getAttribute(userContextKey) != null;
    }

    protected UserContext generateNewContext() {
        return UserHolder.createEmptyContext();
    }

    private UserContext readSecurityContextFromSession(HttpSession httpSession) {
        final boolean debug = logger.isDebugEnabled();

        if (httpSession == null) {
            if (debug) {
                logger.debug("No HttpSession currently exists");
            }

            return null;
        }

        Object contextFromSession = httpSession.getAttribute(userContextKey);

        if (contextFromSession == null) {
            if (debug) {
                logger.debug("HttpSession returned null object for " + userContextKey);
            }

            return null;
        }

        // We now have the security context object from the session.
        if (!(contextFromSession instanceof UserContext)) {
            if (logger.isWarnEnabled()) {
                logger.warn(userContextKey
                        + " did not contain a UserContext but contained: '"
                        + contextFromSession
                        + "'; are you improperly modifying the HttpSession directly "
                        + "(you should always use UserContextHolder ) or using the HttpSession attribute "
                        + "reserved for this class?");
            }

            return null;
        }

        if (debug) {
            logger.debug("Obtained a valid UserContext  from "
                    + userContextKey + ": '" + contextFromSession + "'");
        }

        return (UserContext) contextFromSession;
    }

    public void setUserContextKey(String userContextKey) {
        this.userContextKey = userContextKey;
    }

}
