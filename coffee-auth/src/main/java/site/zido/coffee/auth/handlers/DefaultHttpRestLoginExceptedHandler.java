package site.zido.coffee.auth.handlers;

import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DefaultHttpRestLoginExceptedHandler implements LoginExpectedHandler {
    private String loginExceptedMessage;

    public DefaultHttpRestLoginExceptedHandler(String loginExceptedMessage) {
        this.loginExceptedMessage = loginExceptedMessage;
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
