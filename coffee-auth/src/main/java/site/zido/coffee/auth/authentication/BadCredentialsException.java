package site.zido.coffee.auth.authentication;

public class BadCredentialsException extends AbstractAuthenticationException{
    public BadCredentialsException(String msg, Throwable t) {
        super(msg, t);
    }

    public BadCredentialsException(String msg) {
        super(msg);
    }
}
