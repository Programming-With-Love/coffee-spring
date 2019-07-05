package site.zido.coffee.auth.utils;

import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtils {
    public static void json(HttpServletResponse response, String body) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.getWriter().write(body);
    }
}
