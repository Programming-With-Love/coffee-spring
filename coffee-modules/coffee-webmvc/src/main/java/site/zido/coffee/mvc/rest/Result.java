package site.zido.coffee.mvc.rest;

import site.zido.coffee.mvc.CommonErrorCode;

import java.io.Serializable;
import java.util.Collection;

/**
 * 通用http响应结果
 * <p>
 * 关于result和details为何是同一泛型的说明：
 * <p>
 * result和details为二选一，一般而言，result表示成功数据，而details表示失败细节。
 * 而如果采用两个类型，会显得类型声明太多余。
 * <p>
 * 这里为正确和错误选择同一类型的原因是为后续微服务化能够更方便统一处理有关
 *
 * @param <T> data类型
 * @author zido
 */
public interface Result<T> extends Serializable {

    int getCode();

    String getMessage();

    T getResult();

    Collection<T> getErrors();

    static <T> Result<T> success(T result) {
        DefaultResult<T> response = new DefaultResult<>();
        response.setResult(result);
        return response;
    }

    static <T> Result<T> success() {
        return new DefaultResult<>();
    }

    static <T> Result<T> error() {
        DefaultResult<T> result = new DefaultResult<>();
        result.setCode(CommonErrorCode.UNKNOWN);
        return result;
    }

    static <T> Result<T> error(int code, String message) {
        return error(code, message, null);
    }

    static <T> Result<T> error(int code) {
        return error(code, null);
    }

    static <T> Result<T> error(int code, String message, Collection<T> errors) {
        DefaultResult<T> result = new DefaultResult<>();
        result.setCode(code);
        result.setMessage(message);
        result.setErrors(errors);
        return result;
    }
}
