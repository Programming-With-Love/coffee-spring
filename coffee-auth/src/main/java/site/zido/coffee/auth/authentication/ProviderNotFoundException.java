package site.zido.coffee.auth.authentication;

/**
 * @author zido
 */
public class ProviderNotFoundException extends AbstractAuthenticationException {
    private static final long serialVersionUID = -4602685064624457709L;

    public ProviderNotFoundException(String msg) {
        super(msg);
    }
}
