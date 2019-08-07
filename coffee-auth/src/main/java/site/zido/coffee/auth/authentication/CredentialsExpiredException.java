package site.zido.coffee.auth.authentication;

/**
 * @author zido
 */
public class CredentialsExpiredException extends AccountStatusException {
    private static final long serialVersionUID = -1896846349308106204L;

    public CredentialsExpiredException(String msg, Throwable t) {
        super(msg, t);
    }

    public CredentialsExpiredException(String msg) {
        super(msg);
    }
}
