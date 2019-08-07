package site.zido.coffee.auth.web.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;
import site.zido.coffee.auth.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 用于执行会话固定保护的基类。
 *
 * @author zido
 */
public abstract class AbstractSecuritySessionStrategy implements SecuritySessionStrategy {
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private boolean alwaysCreateSession;

    @Override
    public void onAuthentication(Authentication authentication, HttpServletRequest request, HttpServletResponse response) throws SessionAuthenticationException {
        boolean hasSessionAlready = request.getSession(false) != null;
        if (!hasSessionAlready && !alwaysCreateSession) {
            return;
        }
        HttpSession session = request.getSession();
        if (hasSessionAlready && request.isRequestedSessionIdValid()) {
            String originalSessionId;
            String newSessionId;
            Object mutex = WebUtils.getSessionMutex(session);
            synchronized (mutex) {
                originalSessionId = session.getId();
                session = applySessionFixation(request);
                newSessionId = session.getId();
            }
            if (originalSessionId.equals(newSessionId)) {
                LOGGER.warn("Your servlet container did not change the session ID when a new session was created. You will"
                        + " not be adequately protected against session-fixation attacks");
            }
        }
    }

    public void setAlwaysCreateSession(boolean alwaysCreateSession) {
        this.alwaysCreateSession = alwaysCreateSession;
    }

    protected abstract HttpSession applySessionFixation(HttpServletRequest request);
}
