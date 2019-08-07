package site.zido.coffee.auth.authentication;

public class DisabledException extends AccountStatusException {
    public DisabledException(String msg, Throwable t) {
        super(msg, t);
    }

    public DisabledException(String msg) {
        super(msg);
    }
}
