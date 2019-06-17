package site.zido.coffee.auth.handlers;

import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DefaultDisabledUserRestHandler implements DisabledUserRestHandler {
    private String disabledUserMessage;

    public DefaultDisabledUserRestHandler(String disabledUserMessage) {
        this.disabledUserMessage = disabledUserMessage;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!response.isCommitted()) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            StreamUtils.copy(disabledUserMessage, StandardCharsets.UTF_8, response.getOutputStream());
        }
    }
}
