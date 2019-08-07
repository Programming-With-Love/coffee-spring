package site.zido.coffee.auth.authentication;

public class UsernameNotFoundException extends AbstractAuthenticationException{
    public UsernameNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public UsernameNotFoundException(String msg) {
        super(msg);
    }
}
