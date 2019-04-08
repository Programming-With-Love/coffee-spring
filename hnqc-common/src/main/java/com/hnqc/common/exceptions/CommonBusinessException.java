package com.hnqc.common.exceptions;

/**
 * 业务异常
 *
 * @author zido
 */
public class CommonBusinessException extends RuntimeException {
    private int code;

    public CommonBusinessException(int code, String message) {
        super(message + "(" + code + ")");
        this.code = code;
    }

    public CommonBusinessException(int code) {
        super("unknown error");
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
