package site.zido.coffee.mvc.rest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认rest响应体实现类
 *
 * @author zido
 */
public class DefaultHttpResponseBodyFactory implements HttpResponseBodyFactory {
    @Override
    public boolean isExceptedClass(Class<?> clazz) {
        return Result.class.isAssignableFrom(clazz);
    }

    @Override
    public Object success(Object data) {
        return Result.success(data);
    }

    @Override
    public Object error(int code, String message, Collection<?> errors) {
        return Result.error(code, message, errors);
    }

    @Override
    public Map<String, Object> errorToMap(int code, String message, Collection<?> errors) {
        Map<String, Object> result = new HashMap<>(3);
        result.put("code", code);
        result.put("message", message);
        result.put("errors", errors);
        return result;
    }
}
