package site.zido.coffee.auth.config;

/**
 * {@link AuthBuilder#build()} 只能构建一次
 *
 * @author zido
 */
public class AlreadyBuiltException extends IllegalStateException {

    private static final long serialVersionUID = -8035479505374302953L;

    public AlreadyBuiltException(String message) {
        super(message);
    }

}
