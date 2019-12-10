package site.zido.coffee.common.exceptions;

/**
 * 业务异常
 *
 * @author zido
 */
public class CommonBusinessException extends RuntimeException {
    private static final long serialVersionUID = -8246671096687096493L;
    /**
     * 一般业务异常不建议使用500及以上http status
     */
    private int httpStatus = 400;
    /**
     * 补充错误码，请注意，当允许全局返回{@link site.zido.coffee.common.rest.Result}时，
     * code默认为0，所以不建议占用code为0，请设置其他的code
     */
    private int code = 1;
    private String msg;

    public CommonBusinessException(int httpStatus, int code, String msg) {
        super(buildMsg(httpStatus, code, msg));
    }

    protected CommonBusinessException(Throwable t) {
        super(buildMsg(400, 1, "未知异常"), t);
    }

    public CommonBusinessException(int httpStatus, String msg) {
        this(httpStatus, 1, msg);
    }

    public CommonBusinessException(int httpStatus) {
        this(httpStatus, 1, "未知异常");
    }


    public CommonBusinessException() {
        this(400);
    }

    private static String buildMsg(int httpStatus, int code, String msg) {
        return msg + "(http status:" + httpStatus + ",code:" + code + ")";
    }

    public int getCode() {
        return code;
    }

    protected void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    protected void setMsg(String msg) {
        this.msg = msg;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }
}
