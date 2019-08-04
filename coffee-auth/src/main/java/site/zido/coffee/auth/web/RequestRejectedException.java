package site.zido.coffee.auth.web;

/**
 * 请求不安全
 *
 * @author zido
 */
public class RequestRejectedException extends RuntimeException {
    private static final long serialVersionUID = 5613436699869345956L;

    public RequestRejectedException(String message) {
        super(message);
    }
}
