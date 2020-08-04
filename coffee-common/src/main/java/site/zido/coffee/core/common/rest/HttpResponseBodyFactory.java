package site.zido.coffee.core.common.rest;

import site.zido.coffee.core.common.CommonErrorCode;
import site.zido.coffee.core.common.exceptions.CommonBusinessException;

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
     * @param data    响应数据
     * @return object
     */
    Object error(int code, String message, Object data);

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
     * @param t    ex
     * @param data 响应数据
     * @return object
     */
    default Object error(Throwable t, Object data) {
        if (t instanceof CommonBusinessException) {
            CommonBusinessException cbe = (CommonBusinessException) t;
            return error(cbe.getCode(), cbe.getMsg(), data);
        }
        return error(CommonErrorCode.UNKNOWN, t.getMessage(), data);
    }
}
