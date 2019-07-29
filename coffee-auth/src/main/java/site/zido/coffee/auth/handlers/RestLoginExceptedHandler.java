package site.zido.coffee.auth.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StreamUtils;
import site.zido.coffee.auth.Constants;
import site.zido.coffee.common.CommonErrorCode;
import site.zido.coffee.common.rest.HttpResponseBodyFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RestLoginExceptedHandler implements LoginExpectedHandler {
    private String loginExceptedMessage;

    public RestLoginExceptedHandler(HttpResponseBodyFactory responseBodyFactory) {
        this(responseBodyFactory, new ObjectMapper());
    }

    public RestLoginExceptedHandler(HttpResponseBodyFactory responseBodyFactory, ObjectMapper mapper) {
        this(responseBodyFactory.error(
                Constants.ERROR_CODE_NO_PERMISSIONS,
                "forbidden!",
                null), mapper);
    }

    public RestLoginExceptedHandler(Object result, ObjectMapper mapper) {
        try {
            loginExceptedMessage = mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!response.isCommitted()) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            StreamUtils.copy(loginExceptedMessage, StandardCharsets.UTF_8, response.getOutputStream());
        }
    }
}
