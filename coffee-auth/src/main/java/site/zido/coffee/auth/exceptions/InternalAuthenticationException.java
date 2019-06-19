package site.zido.coffee.auth.exceptions;

public class InternalAuthenticationException extends AuthenticationException {

    public InternalAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalAuthenticationException(String message) {
        super(message);
    }
}
