package site.zido.coffee.auth.exceptions;

public class NoSuchUserException extends AuthenticationException {
    public NoSuchUserException() {
        super("用户不存在");
    }
}
