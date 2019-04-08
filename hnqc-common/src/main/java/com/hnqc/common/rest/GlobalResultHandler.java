package com.hnqc.common.rest;


import com.hnqc.common.pojo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
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
public class GlobalResultHandler implements ResponseBodyAdvice {
    private static Logger LOGGER = LoggerFactory.getLogger(GlobalResultHandler.class);

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return returnType.getMethod().getReturnType() != Result.class;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        OriginalResponse methodAnnotation = returnType.getMethodAnnotation(OriginalResponse.class);
        if (methodAnnotation != null || returnType.getDeclaringClass().getAnnotation(OriginalResponse.class) != null) {
            if (body instanceof String && !((String) body).startsWith("o:")) {
                return "o:" + body;
            }
            return body;
        }
        Class<?> returnClass = returnType.getMethod().getReturnType();
        if (body instanceof Result || body instanceof String || (returnClass.equals(String.class) && body == null)) {
            return body;
        }
        return Result.success(body);
    }

}
