package site.zido.coffee.autoconfigure.web.basic;

import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import site.zido.coffee.core.message.CoffeeMessageSource;
import site.zido.coffee.mvc.CommonErrorCode;
import site.zido.coffee.mvc.rest.HttpResponseBodyFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import static site.zido.coffee.mvc.rest.GlobalResultHandler.ORIGINAL_RESPONSE;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class CoffeeErrorAttributes implements ErrorAttributes, HandlerExceptionResolver, Ordered {
    private static final String ERROR_ATTRIBUTE = CoffeeErrorAttributes.class.getName() + ".ERROR";
    private static final MessageSourceAccessor messages = CoffeeMessageSource.getAccessor();

    private final boolean includeException;

    private final HttpResponseBodyFactory factory;

    public CoffeeErrorAttributes(HttpResponseBodyFactory factory) {
        this(false, factory);
    }

    public CoffeeErrorAttributes(boolean includeException, HttpResponseBodyFactory factory) {
        this.includeException = includeException;
        this.factory = factory;
    }

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        Integer status = getAttribute(webRequest, "javax.servlet.error.status_code");
        if (status == null) {
            return factory.errorToMap(CommonErrorCode.UNKNOWN,
                    messages.getMessage("UNKNOWN_ERROR", "未知异常"),
                    null);
        }
        Throwable error = getError(webRequest);
        if (error != null) {
            while (error instanceof ServletException && error.getCause() != null) {
                error = error.getCause();
            }
        }
        List<String> errors = null;
        if (error instanceof BindingResult) {
            errors = parseBindingResult((BindingResult) error);
        }
        if (error instanceof MethodArgumentNotValidException) {
            errors = parseBindingResult(((MethodArgumentNotValidException) error).getBindingResult());
        }
        if (errors != null) {
            return factory.errorToMap(CommonErrorCode.VALIDATION_FAILED,
                    messages.getMessage("VALIDATION_FAILED", "数据校验错误"),
                    errors);
        }
        if (error != null) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("message", error.getMessage());
            while (error instanceof ServletException && error.getCause() != null) {
                error = error.getCause();
            }
            if (this.includeException) {
                detail.put("exception", error.getClass().getName());
            }
            if (includeStackTrace) {
                detail.put("trace", getStackTrace(error));
            }
            return factory.errorToMap(CommonErrorCode.UNKNOWN,
                    error.getMessage(), Collections.singleton(detail));
        }
        String message;
        try {
            message = HttpStatus.valueOf(status).getReasonPhrase();
        } catch (Exception ex) {
            message = messages.getMessage("UNKNOWN_ERROR", "未知异常") + "(Http Status " + status + ")";
        }

        return factory.errorToMap(CommonErrorCode.UNKNOWN,
                message,
                null);
    }

    private String getStackTrace(Throwable error) {
        StringWriter stackTrace = new StringWriter();
        error.printStackTrace(new PrintWriter(stackTrace));
        stackTrace.flush();
        return stackTrace.toString();
    }

    private List<String> parseBindingResult(BindingResult result) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : result.getFieldErrors()) {
            String name = error.getField();
            String message = error.getDefaultMessage();
            message = "[" + name + "] " + message;
            errors.add(message);
        }
        return errors;
    }

    @Override
    public Throwable getError(WebRequest webRequest) {
        Throwable exception = getAttribute(webRequest, ERROR_ATTRIBUTE);
        if (exception == null) {
            exception = getAttribute(webRequest, "javax.servlet.error.exception");
        }
        return exception;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @SuppressWarnings("unchecked")
    private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
        return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        storeErrorAttributes(request, ex);
        return null;
    }

    private void storeErrorAttributes(HttpServletRequest request, Exception ex) {
        request.setAttribute(ERROR_ATTRIBUTE, ex);
    }
}
