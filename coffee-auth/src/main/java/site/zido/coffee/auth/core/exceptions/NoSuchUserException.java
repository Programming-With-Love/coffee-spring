package site.zido.coffee.auth.core.exceptions;

import site.zido.coffee.auth.authentication.AbstractAuthenticationException;

/**
 * 用户不存在
 *
 * @author zido
 */
public class NoSuchUserException extends AbstractAuthenticationException {
    private static final long serialVersionUID = -4407882328094865280L;

    public NoSuchUserException() {
        super("用户不存在");
    }
}
