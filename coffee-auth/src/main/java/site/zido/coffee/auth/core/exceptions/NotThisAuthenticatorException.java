package site.zido.coffee.auth.core.exceptions;

import site.zido.coffee.auth.authentication.AbstractAuthenticationException;

/**
 * 认证器选择错误,主要用于在遍历运行认证器时的逻辑处理
 *
 * @author zido
 */
public class NotThisAuthenticatorException extends AbstractAuthenticationException {
    public NotThisAuthenticatorException() {
        super("not this authenticator");
    }
}
