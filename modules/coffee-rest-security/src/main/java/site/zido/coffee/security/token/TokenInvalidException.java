package site.zido.coffee.security.token;

/**
 * token 失效异常
 *
 * @author zido
 */
public class TokenInvalidException extends RuntimeException {
    public TokenInvalidException(String msg, Throwable t) {
        super(msg, t);
    }

    public TokenInvalidException(String msg) {
        super(msg);
    }
}
