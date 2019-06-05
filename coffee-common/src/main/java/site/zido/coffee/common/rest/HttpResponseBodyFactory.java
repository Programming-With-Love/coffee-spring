package site.zido.coffee.common.rest;

import site.zido.coffee.common.CommonErrorCode;
import site.zido.coffee.common.exceptions.CommonBusinessException;

/**
 * http 相应结果生成工厂
 */
public interface HttpResponseBodyFactory {
    /**
     * 是否是期望的类型
     *
     * @param clazz 目标类型
     * @return true/false
     */
    boolean isExceptedClass(Class<?> clazz);

    Object success(Object data);

    Object error(int code, String message, Object data);

    default Object error(Throwable t, Object data) {
        if (t instanceof CommonBusinessException) {
            CommonBusinessException cbe = (CommonBusinessException) t;
            return error(cbe.getCode(), cbe.getMsg(), data);
        }
        return error(CommonErrorCode.UNKNOWN, t.getMessage(), data);
    }
}
