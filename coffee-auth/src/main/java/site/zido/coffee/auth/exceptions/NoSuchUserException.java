package site.zido.coffee.auth.exceptions;

/**
 * 用户不存在
 *
 * @author zido
 */
public class NoSuchUserException extends AbstractAuthenticationException {
    public NoSuchUserException() {
        super("用户不存在");
    }
}
