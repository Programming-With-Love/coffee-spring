package site.zido.coffee.core.utils;

/**
 * BeanUtils exception.
 *
 * @author zido
 */
public class BeanUtilsException extends RuntimeException {

    public BeanUtilsException(String message) {
        super(message);
    }

    public BeanUtilsException(String message, Throwable cause) {
        super(message, cause);
    }
}
