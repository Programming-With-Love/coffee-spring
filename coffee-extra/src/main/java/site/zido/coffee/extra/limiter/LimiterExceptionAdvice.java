package site.zido.coffee.extra.limiter;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.zido.coffee.common.CommonErrorCode;
import site.zido.coffee.common.rest.Result;

@RestControllerAdvice
@Order(0)
public class LimiterExceptionAdvice {
    @ExceptionHandler(LimiterException.class)
    public Result<?> handleLimiterException(LimiterException e) {
        return Result.error(CommonErrorCode.LIMIT, e.getMessage());
    }
}
