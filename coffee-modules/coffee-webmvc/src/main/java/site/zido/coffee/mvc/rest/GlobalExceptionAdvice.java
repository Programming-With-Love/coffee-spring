package site.zido.coffee.mvc.rest;


import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import site.zido.coffee.mvc.exceptions.CommonBusinessException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

/**
 * server 异常处理,兜底处理
 *
 * @author zido
 */
@RestControllerAdvice
@Order
public class GlobalExceptionAdvice extends BaseGlobalExceptionHandler {
    public GlobalExceptionAdvice(HttpResponseBodyFactory factory) {
        super(factory);
    }

    @ExceptionHandler(CommonBusinessException.class)
    @Override
    protected ResponseEntity<Object> handleCommonBusinessException(CommonBusinessException e, WebRequest request, HttpServletResponse response) {
        return super.handleCommonBusinessException(e, request, response);
    }

}
