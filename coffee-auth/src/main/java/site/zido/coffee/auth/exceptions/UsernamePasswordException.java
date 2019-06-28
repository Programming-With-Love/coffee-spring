package site.zido.coffee.auth.exceptions;

public class UsernamePasswordException extends AuthenticationException {
    public UsernamePasswordException() {
        super("账号或密码错误");
    }
}
