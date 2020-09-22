package site.zido.coffee.mvc.rest;

import org.springframework.http.ResponseEntity;

import java.util.Collection;

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
}
