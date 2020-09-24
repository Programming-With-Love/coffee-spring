package site.zido.coffee.mvc.rest;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;

/**
 * 统一全局响应封装
 *
 * @author zido
 */
@RestControllerAdvice
public class GlobalResultHandler implements ResponseBodyAdvice<Object> {
    public static final String ORIGINAL_RESPONSE = "ORIGINAL_RESPONSE";
    private final HttpResponseBodyFactory factory;

    public GlobalResultHandler(HttpResponseBodyFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        //支持Optional包装
        return !factory.isExceptedClass(returnType.nestedIfOptional().getParameterType());
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        //TODO 跳过/error错误处理，这与errorController冲突
        OriginalResponse methodAnnotation = returnType.getMethodAnnotation(OriginalResponse.class);
        if (methodAnnotation != null || returnType.getDeclaringClass().getAnnotation(OriginalResponse.class) != null) {
            return body;
        }
        Method method = returnType.getMethod();
        if (method == null) {
            return null;
        }
        Class<?> returnClass = method.getReturnType();
        if (returnClass.equals(String.class) && body == null) {
            return null;
        }
        return factory.success(body);
    }

}
