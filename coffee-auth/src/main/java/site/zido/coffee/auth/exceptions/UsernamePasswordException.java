package site.zido.coffee.auth.exceptions;

/**
 * 用户名密码错误
 *
 * @author zido
 */
public class UsernamePasswordException extends AbstractAuthenticationException {
    public UsernamePasswordException() {
        super("账号或密码错误");
    }
}
