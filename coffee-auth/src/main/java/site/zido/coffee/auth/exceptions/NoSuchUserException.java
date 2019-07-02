package site.zido.coffee.auth.exceptions;

/**
 * 用户不存在
 *
 * @author zido
 */
public class NoSuchUserException extends AuthenticationException {
    public NoSuchUserException() {
        super("用户不存在");
    }
}
