package site.zido.coffee.auth.exceptions;

public class NotThisAuthenticatorException extends AuthenticationException{
    public NotThisAuthenticatorException() {
        super("not this authenticator");
    }
}
