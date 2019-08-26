package site.zido.coffee.common.rest;


import site.zido.coffee.common.CommonErrorCode;
import site.zido.coffee.common.exceptions.CommonBusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 统一异常处理
 *
 * @author zido
 */
public abstract class BaseGlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseGlobalExceptionHandler.class);
    private static final String HANDLE_EXCEPTION_TEMPLATE = "handle %s,url:%s,caused by:";

    protected Result<?> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        Iterator<ConstraintViolation<?>> iterator = constraintViolations.iterator();
        if (iterator.hasNext()) {
            ConstraintViolation<?> next = iterator.next();
            Path propertyPath = next.getPropertyPath();
            String name = "unknown";
            //获取参数名
            for (Path.Node node : propertyPath) {
                name = node.getName();
            }
            //参数错误提示
            String message = "[" + name + "] " + next.getMessage();
            return Result.error(CommonErrorCode.INVALID_PARAMETERS, message);
        }
        return Result.error(CommonErrorCode.INVALID_PARAMETERS);
    }

    protected Result<?> handleConstraintViolationException(HttpMessageNotReadableException e, HttpServletRequest request) {
        return Result.error(CommonErrorCode.INVALID_PARAMETERS, e.getMessage());
    }

    protected Result<?> handleBindException(BindException e, HttpServletRequest request) {
        BindingResult bindingResult = e.getBindingResult();
        return parseBindingResult(bindingResult);
    }

    protected Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        return parseBindingResult(e.getBindingResult());
    }

    protected Result<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        logWithTemplate(e.getClass().getName(), request, e);
        return Result.error(CommonErrorCode.UNKNOWN, e.getMessage());
    }

    protected Result<?> handleCommonBusinessException(CommonBusinessException e, HttpServletRequest request) {
        LOGGER.warn("business error:" + e.getMessage());
        return Result.error(e.getCode(), e.getMsg());
    }

    private Result<?> parseBindingResult(BindingResult bindingResult) {
        List<FieldError> errors = bindingResult.getFieldErrors();
        if (errors.size() > 0) {
            //仅获取第一个异常
            FieldError next = errors.get(0);
            String name = next.getField();
            String message = next.getDefaultMessage();
            message = "[" + name + "] " + message;
            return Result.error(CommonErrorCode.INVALID_PARAMETERS, message);
        }
        return Result.error(CommonErrorCode.INVALID_PARAMETERS);
    }

    private void logWithTemplate(String exceptionName, HttpServletRequest request, Throwable e) {
        LOGGER.error(String.format(HANDLE_EXCEPTION_TEMPLATE, exceptionName, request.getRequestURI()), e);
    }
}
