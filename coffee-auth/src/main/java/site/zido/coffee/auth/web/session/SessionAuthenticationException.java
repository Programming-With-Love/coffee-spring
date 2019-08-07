package site.zido.coffee.auth.web.session;

import site.zido.coffee.auth.authentication.AbstractAuthenticationException;

/**
 * @author zido
 */
public class SessionAuthenticationException extends AbstractAuthenticationException {
    private static final long serialVersionUID = 4964331876187199450L;

    public SessionAuthenticationException(String msg) {
        super(msg);
    }
}
