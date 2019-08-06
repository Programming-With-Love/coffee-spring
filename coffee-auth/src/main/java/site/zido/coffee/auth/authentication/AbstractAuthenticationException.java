package site.zido.coffee.auth.authentication;

/**
 * 认证错误
 *
 * @author zido
 */
public abstract class AbstractAuthenticationException extends RuntimeException {

    private static final long serialVersionUID = 7109623910705254563L;

    public AbstractAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public AbstractAuthenticationException(String msg) {
        super(msg);
    }

}
