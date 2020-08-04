package site.zido.coffee.core.common.rest;


import site.zido.coffee.core.common.CommonErrorCode;

import java.io.Serializable;

/**
 * 通用http响应结果
 *
 * @param <T> data类型
 * @author zido
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -3266931205943696705L;
    private T result;
    private int code = 0;
    private String message;

    public static <T> Result<T> success(T result) {
        Result<T> response = new Result<>();
        response.result = result;
        return response;
    }

    public static <T> Result<T> success() {
        return new Result<>();
    }

    public static <T> Result<T> error() {
        Result<T> result = new Result<>();
        result.code = CommonErrorCode.UNKNOWN;
        return result;
    }

    public static <T> Result<T> error(int code, String message) {
        return error(code, message, null);
    }

    public static <T> Result<T> error(int code) {
        return error(code, null);
    }

    public static <T> Result<T> error(int code, String message, T data) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        result.result = data;
        return result;
    }

    public T getResult() {
        return result;
    }

    public Result<T> setResult(T result) {
        this.result = result;
        return this;
    }

    public int getCode() {
        return code;
    }

    public Result<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }
}
