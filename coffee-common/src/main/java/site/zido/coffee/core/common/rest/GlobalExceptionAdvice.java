package site.zido.coffee.core.common.rest;


import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.zido.coffee.core.common.exceptions.CommonBusinessException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

/**
 * server 异常处理,兜底处理
 *
 * @author zido
 */
@RestControllerAdvice
public class GlobalExceptionAdvice extends BaseGlobalExceptionHandler {
    public GlobalExceptionAdvice(HttpResponseBodyFactory factory) {
        super(factory);
    }

    /**
     * dto参数校验异常处理
     *
     * @param e 校验异常
     * @return result
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "参数非法")
    @Override
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        return super.handleMethodArgumentNotValidException(e, request);
    }

    @ExceptionHandler(value = BindException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "参数非法")
    @Override
    protected Object handleBindException(BindException e, HttpServletRequest request) {
        return super.handleBindException(e, request);
    }

    /**
     * parameter参数校验异常处理
     *
     * @param e 校验异常
     * @return result
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "参数非法")
    @Override
    public Object handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        return super.handleConstraintViolationException(e, request);
    }

    /**
     * parameter参数校验异常处理
     *
     * @param e 校验异常
     * @return result
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "参数非法")
    @Override
    public Object handleConstraintViolationException(HttpMessageNotReadableException e, HttpServletRequest request) {
        return super.handleConstraintViolationException(e, request);
    }

    @ExceptionHandler(CommonBusinessException.class)
    @Override
    protected Object handleCommonBusinessException(CommonBusinessException e, HttpServletRequest request, HttpServletResponse response) {
        return super.handleCommonBusinessException(e, request, response);
    }

}
