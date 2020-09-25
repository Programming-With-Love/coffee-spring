package site.zido.coffee.autoconfigure.extra.limiter;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.zido.coffee.extra.limiter.LimiterException;
import site.zido.coffee.mvc.CommonErrorCode;
import site.zido.coffee.mvc.rest.HttpResponseBodyFactory;
import site.zido.coffee.mvc.rest.OriginalResponse;

@RestControllerAdvice
@Order(0)
public class LimiterExceptionAdvice {
    private final HttpResponseBodyFactory factory;

    public LimiterExceptionAdvice(HttpResponseBodyFactory factory) {
        this.factory = factory;
    }

    @ExceptionHandler(LimiterException.class)
    @OriginalResponse
    public ResponseEntity<Object> handleLimiterException(LimiterException e) {
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(factory.error(CommonErrorCode.LIMIT, e.getMessage()));
    }
}
