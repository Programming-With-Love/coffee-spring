package site.zido.coffee.mvc.rest;


import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
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
    private final HttpResponseBodyFactory factory;
    private final MessageSourceAccessor messages = CoffeeMessageSource.getAccessor();

    protected BaseGlobalExceptionHandler(HttpResponseBodyFactory factory) {
        Assert.notNull(factory, "http response body factory cannot be null");
        this.factory = factory;
    }

    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e, WebRequest request) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        Iterator<ConstraintViolation<?>> iterator = constraintViolations.iterator();
        List<String> errors = new ArrayList<>();
        while (iterator.hasNext()) {
            ConstraintViolation<?> next = iterator.next();
            Path propertyPath = next.getPropertyPath();
            String name = "unknown";
            //获取参数名
            for (Path.Node node : propertyPath) {
                name = node.getName();
            }
            //参数错误提示
            String message = "[" + name + "] " + next.getMessage();
            errors.add(message);
        }
        return handleExceptionInternal(
                e,
                factory.error(CommonErrorCode.VALIDATION_FAILED,
                        messages.getMessage("ValidationFailed", "Validation Failed"),
                        errors),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    protected ResponseEntity<Object> handleBindException(BindException e, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : e.getFieldErrors()) {
            String name = error.getField();
            String message = error.getDefaultMessage();
            message = "[" + name + "] " + message;
            errors.add(message);
        }
        HttpStatus status = HttpStatus.BAD_REQUEST;
        HttpHeaders headers = new HttpHeaders();
        return handleExceptionInternal(
                e,
                factory.error(CommonErrorCode.VALIDATION_FAILED, null, errors),
                headers,
                status,
                request
        );
    }

    protected Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            String name = error.getField();
            String message = error.getDefaultMessage();
            message = "[" + name + "] " + message;
            errors.add(message);
        }
        HttpStatus status = HttpStatus.BAD_REQUEST;
        HttpHeaders headers = new HttpHeaders();
        return handleExceptionInternal(
                e,
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
