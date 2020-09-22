package site.zido.coffee.mvc.rest;

import java.util.Collection;

public class DefaultResult<T> implements Result<T> {
    private static final long serialVersionUID = -3266931205943696705L;
    private int code = 0;
    private String message;
    private T result;
    private Collection<T> errors;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public Collection<T> getErrors() {
        return errors;
    }

    public void setErrors(Collection<T> errors) {
        this.errors = errors;
    }
}
