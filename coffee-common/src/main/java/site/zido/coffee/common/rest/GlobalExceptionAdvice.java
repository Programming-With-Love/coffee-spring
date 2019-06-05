package site.zido.coffee.common.rest;


import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.zido.coffee.common.exceptions.CommonBusinessException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

/**
 * server 异常处理,兜底处理
 *
 * @author zido
 */
@RestControllerAdvice
@Order
public class GlobalExceptionAdvice extends BaseGlobalExceptionHandler {
    /**
     * dto参数校验异常处理
     *
     * @param e 校验异常
     * @return result
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @Override
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        return super.handleMethodArgumentNotValidException(e, request);
    }

    @ExceptionHandler(value = BindException.class)
    @Override
    protected Result<?> handleBindException(BindException e, HttpServletRequest request) {
        return super.handleBindException(e, request);
    }

    /**
     * parameter参数校验异常处理
     *
     * @param e 校验异常
     * @return result
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @Override
    public Result<?> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        return super.handleConstraintViolationException(e, request);
    }

    /**
     * parameter参数校验异常处理
     *
     * @param e 校验异常
     * @return result
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @Override
    public Result<?> handleConstraintViolationException(HttpMessageNotReadableException e, HttpServletRequest request) {
        return super.handleConstraintViolationException(e, request);
    }

    @ExceptionHandler(RuntimeException.class)
    @Override
    protected Result<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        return super.handleRuntimeException(e, request);
    }

    @ExceptionHandler(CommonBusinessException.class)
    @Override
    protected Result<?> handleCommonBusinessException(CommonBusinessException e, HttpServletRequest request) {
        return super.handleCommonBusinessException(e, request);
    }

}
