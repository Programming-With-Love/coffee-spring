package site.zido.coffee.mvc.rest;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import site.zido.coffee.core.message.CoffeeMessageSource;
import site.zido.coffee.mvc.CommonErrorCode;
import site.zido.coffee.mvc.exceptions.CommonBusinessException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 统一异常处理
 *
 * @author zido
 */
public abstract class BaseGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    protected final HttpResponseBodyFactory factory;
    protected final MessageSourceAccessor messages = CoffeeMessageSource.getAccessor();

    protected BaseGlobalExceptionHandler(HttpResponseBodyFactory factory) {
        Assert.notNull(factory, "http response body factory cannot be null");
        this.factory = factory;
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(
                ex,
                factory.error(CommonErrorCode.VALIDATION_FAILED, null, parseBindingResult(ex)),
                headers,
                status,
                request
        );
    }

    public static List<String> parseBindingResult(BindingResult ex) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getFieldErrors()) {
            String name = error.getField();
            String message = error.getDefaultMessage();
            message = "[" + name + "] " + message;
            errors.add(message);
        }
        return errors;
    }

    /**
     * dto参数校验异常处理
     *
     * @param ex 校验异常
     * @return result
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String name = error.getField();
            String message = error.getDefaultMessage();
            message = "[" + name + "] " + message;
            errors.add(message);
        }
        return handleExceptionInternal(
                ex,
                factory.error(CommonErrorCode.VALIDATION_FAILED, null, errors),
                headers,
                status,
                request
        );
    }

    protected ResponseEntity<Object> handleCommonBusinessException(CommonBusinessException e, WebRequest request, HttpServletResponse response) {
        logger.warn("business error:" + e.getMessage());
        response.setStatus(e.getHttpStatus());
        HttpStatus status = HttpStatus.valueOf(e.getHttpStatus());
        HttpHeaders headers = new HttpHeaders();
        return handleExceptionInternal(
                e,
                factory.error(e.getCode(), e.getMsg(), null),
                headers,
                status,
                request
        );
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (body == null) {
            body = factory.error(ex);
        }
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }
}
