package site.zido.coffee.auth.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StreamUtils;
import site.zido.coffee.auth.Constants;
import site.zido.coffee.common.rest.HttpResponseBodyFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author zido
 */
public class RestDisabledUserHandler implements DisabledUserHandler {
    private String disabledUserMessage;

    public RestDisabledUserHandler(HttpResponseBodyFactory responseBodyFactory) {
        this(responseBodyFactory, new ObjectMapper());
    }

    public RestDisabledUserHandler(HttpResponseBodyFactory responseBodyFactory, ObjectMapper mapper) {
        this(responseBodyFactory.error(
                Constants.ERROR_CODE_USER_IS_DISABLED,
                "user is disabled!",
                null), mapper);
    }

    public RestDisabledUserHandler(Object result, ObjectMapper mapper) {
        try {
            disabledUserMessage = mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!response.isCommitted()) {
            response.setContentType("application/json; charset=utf-8");
            StreamUtils.copy(disabledUserMessage, StandardCharsets.UTF_8, response.getOutputStream());
        }
    }
}
