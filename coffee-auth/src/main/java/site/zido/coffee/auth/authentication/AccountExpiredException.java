package site.zido.coffee.auth.authentication;

/**
 * @author zido
 */
public class AccountExpiredException extends AccountStatusException {
    private static final long serialVersionUID = -4414679418075267545L;

    public AccountExpiredException(String msg, Throwable t) {
        super(msg, t);
    }

    public AccountExpiredException(String msg) {
        super(msg);
    }
}
