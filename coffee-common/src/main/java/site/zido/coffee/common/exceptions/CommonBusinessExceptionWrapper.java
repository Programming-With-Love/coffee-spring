package site.zido.coffee.common.exceptions;

/**
 * common business exception wrapper
 *
 * @author zido
 */
public class CommonBusinessExceptionWrapper extends CommonBusinessException {
    private static final long serialVersionUID = -3391089354972290116L;

    public CommonBusinessExceptionWrapper(Throwable t) {
        super(t);
        if (t instanceof CommonBusinessException) {
            setCode(((CommonBusinessException) t).getCode());
            setMsg(((CommonBusinessException) t).getMsg());
        }
    }

    public CommonBusinessExceptionWrapper(int code, String message) {
        super(code, message);
    }
}
