package site.zido.coffee.auth.exceptions;

/**
 * 鉴权时发生的内部异常
 *
 * @author zido
 */
public class InternalAuthenticationException extends AuthenticationException {

    public InternalAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalAuthenticationException(String message) {
        super(message);
    }
}
