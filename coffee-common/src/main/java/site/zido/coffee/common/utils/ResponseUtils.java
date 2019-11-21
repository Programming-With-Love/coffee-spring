package site.zido.coffee.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 响应工具类
 *
 * @author zido
 */
public class ResponseUtils {
    private static ObjectMapper mapper = new ObjectMapper();

    public static void json(HttpServletResponse response, Object body) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        if (body != null) {
            response.getWriter().write(mapper.writeValueAsString(body));
        }
    }

    public static void json(HttpServletResponse response, String body) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        if (body != null) {
            response.getWriter().write(body);
        }
    }

    public static void json(HttpServletResponse response, int status) throws IOException {
        json(response, status, null);
    }

    public static void json(HttpServletResponse response, int status, String body) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        if (body != null) {
            response.getWriter().write(body);
        }
    }

    public static void json(HttpServletResponse response, int status, Object body) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        if (body != null) {
            response.getWriter().write(mapper.writeValueAsString(body));
        }
    }
}
