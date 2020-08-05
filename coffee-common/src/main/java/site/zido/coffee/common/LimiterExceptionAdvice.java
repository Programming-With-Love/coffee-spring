package site.zido.coffee.common;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.zido.coffee.extra.limiter.LimiterException;
import site.zido.coffee.mvc.CommonErrorCode;
import site.zido.coffee.mvc.rest.Result;

@RestControllerAdvice
@Order(0)
public class LimiterExceptionAdvice {
    @ExceptionHandler(LimiterException.class)
    public Result<?> handleLimiterException(LimiterException e) {
        return Result.error(CommonErrorCode.LIMIT, e.getMessage());
    }
}
