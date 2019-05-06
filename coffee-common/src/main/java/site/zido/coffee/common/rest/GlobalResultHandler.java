package site.zido.coffee.common.rest;

import site.zido.coffee.common.pojo.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一全局响应封装
 *
 * @author zido
 */
@RestControllerAdvice
public class GlobalResultHandler implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getMethod().getReturnType() != Result.class;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        OriginalResponse methodAnnotation = returnType.getMethodAnnotation(OriginalResponse.class);
        if (methodAnnotation != null || returnType.getDeclaringClass().getAnnotation(OriginalResponse.class) != null) {
            return body;
        }
        Class<?> returnClass = returnType.getMethod().getReturnType();
        if (body instanceof Result || (returnClass.equals(String.class) && body == null)) {
            return body;
        }
        return Result.success(body);
    }

}
