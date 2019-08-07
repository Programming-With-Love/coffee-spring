package site.zido.coffee.auth.authentication;

public class LockedException extends AccountStatusException {
    public LockedException(String msg, Throwable t) {
        super(msg, t);
    }

    public LockedException(String msg) {
        super(msg);
    }
}
