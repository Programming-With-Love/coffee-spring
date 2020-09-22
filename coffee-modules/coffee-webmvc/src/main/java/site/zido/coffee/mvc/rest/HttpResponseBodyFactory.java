package site.zido.coffee.mvc.rest;


import org.springframework.http.ResponseEntity;
import site.zido.coffee.mvc.CommonErrorCode;
import site.zido.coffee.mvc.exceptions.CommonBusinessException;

import java.util.Collection;

/**
 * http 相应结果生成工厂
 *
 * @author zido
 */
public interface HttpResponseBodyFactory {
    /**
     * 是否是期望的类型
     *
     * @param clazz 目标类型
     * @return true/false
     */
    boolean isExceptedClass(Class<?> clazz);

    /**
     * 成功
     *
     * @param data 响应数据
     * @return object
     */
    Object success(Object data);

    /**
     * 失败
     *
     * @param code    code
     * @param message message
     * @param errors 异常详细信息，可选
     * @return object
     */
    Object error(int code, String message, Collection<?> errors);

    /**
     * 失败
     *
     * @param code    code
     * @param message message
     * @return object
     */
    default Object error(int code, String message) {
        return error(code, message, null);
    }

    /**
     * 失败
     *
     * @param t ex
     * @return object
     */
    default Object error(Throwable t) {
        if (t instanceof CommonBusinessException) {
            CommonBusinessException cbe = (CommonBusinessException) t;
            return error(cbe.getCode(), cbe.getMsg(), null);
        }
        return error(CommonErrorCode.UNKNOWN, t.getMessage(), null);
    }
}
