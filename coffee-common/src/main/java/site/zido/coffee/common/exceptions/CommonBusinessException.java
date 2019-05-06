package site.zido.coffee.common.exceptions;

/**
 * 业务异常
 *
 * @author zido
 */
public class CommonBusinessException extends RuntimeException {
    private static final long serialVersionUID = -8246671096687096493L;
    private int code;
    private String msg;

    public CommonBusinessException(int code, String msg) {
        super(msg + "(" + code + ")");
        this.code = code;
        this.msg = msg;
    }

    public CommonBusinessException(int code) {
        super("未知异常");
        this.code = code;
        this.msg = "未知异常";
    }

    protected CommonBusinessException(Throwable t){
        super(t);
    }

    public int getCode() {
        return code;
    }

    protected void setCode(int code) {
        this.code = code;
    }

    public String getMsg(){
        return msg;
    }

    protected void setMsg(String msg) {
        this.msg = msg;
    }
}
