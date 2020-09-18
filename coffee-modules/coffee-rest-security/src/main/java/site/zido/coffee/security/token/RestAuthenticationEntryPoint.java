package site.zido.coffee.security.token;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zido
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private MediaType mediaType;
    private String content;

    public RestAuthenticationEntryPoint() {
    }

    public RestAuthenticationEntryPoint(String content) {
        this(MediaType.APPLICATION_JSON, content);
    }

    public RestAuthenticationEntryPoint(MediaType mediaType, String content) {
        this.mediaType = mediaType;
        this.content = content;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        if (this.mediaType != null) {
            response.setHeader("Content-Type", this.mediaType.toString());
        }
        if (StringUtils.hasLength(content)) {
            response.getWriter().write(content);
        }
    }
}
