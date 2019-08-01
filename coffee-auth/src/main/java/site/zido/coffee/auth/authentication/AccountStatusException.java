package site.zido.coffee.auth.authentication;

/**
 * 表示用户状态异常的基类
 *
 * @author zido
 */
public abstract class AccountStatusException extends AbstractAuthenticationException {
    private static final long serialVersionUID = 761037787491086986L;

    public AccountStatusException(String msg, Throwable t) {
        super(msg, t);
    }

    public AccountStatusException(String msg) {
        super(msg);
    }
}
