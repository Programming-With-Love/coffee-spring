package site.zido.coffee.security.token;

/**
 * token 失效异常
 *
 * @author zido
 */
public class TokenInvalidException extends Exception {
    public TokenInvalidException(String msg, Throwable t) {
        super(msg, t);
    }
}
