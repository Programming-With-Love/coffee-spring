package site.zido.coffee.mvc.rest;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import site.zido.coffee.mvc.CommonErrorCode;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@RestControllerAdvice
@Order
public class JavaxValidationExceptionAdvice extends BaseGlobalExceptionHandler {
    public JavaxValidationExceptionAdvice(HttpResponseBodyFactory factory) {
        super(factory);
    }

    /**
     * parameter参数校验异常处理
     *
     * @param e 校验异常
     * @return result
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e, WebRequest request) {
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
}
