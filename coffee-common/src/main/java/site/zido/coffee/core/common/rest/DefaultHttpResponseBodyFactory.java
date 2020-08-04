package site.zido.coffee.core.common.rest;

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
    public Object error(int code, String message, Object data) {
        return Result.error(code, message, data);
    }
}
