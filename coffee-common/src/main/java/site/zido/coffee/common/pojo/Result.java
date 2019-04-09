package site.zido.coffee.common.pojo;


import site.zido.coffee.common.CommonErrorCode;

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
    private boolean success = true;
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
        result.success = false;
        return result;
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.success = false;
        result.code = code;
        result.message = message;
        return result;
    }

    public static <T> Result<T> error(int code) {
        Result<T> result = new Result<>();
        result.success = false;
        result.code = code;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.success = false;
        result.message = msg;
        return result;
    }

    public static <T> Result<T> print(boolean success) {
        if(success){
            return success();
        }else{
            return error();
        }
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

    public boolean isSuccess() {
        return success;
    }

    public Result<T> setSuccess(boolean success) {
        this.success = success;
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
