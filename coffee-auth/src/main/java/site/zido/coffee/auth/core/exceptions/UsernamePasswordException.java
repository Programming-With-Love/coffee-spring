package site.zido.coffee.auth.core.exceptions;

import site.zido.coffee.auth.authentication.AbstractAuthenticationException;

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
