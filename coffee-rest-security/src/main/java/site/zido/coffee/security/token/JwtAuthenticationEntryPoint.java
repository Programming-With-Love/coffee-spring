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
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private MediaType mediaType;
    private String successBody;

    public JwtAuthenticationEntryPoint() {
    }

    public JwtAuthenticationEntryPoint(String successBody) {
        this(MediaType.APPLICATION_JSON, successBody);
    }

    public JwtAuthenticationEntryPoint(MediaType mediaType, String successBody) {
        this.mediaType = mediaType;
        this.successBody = successBody;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public void setSuccessBody(String successBody) {
        this.successBody = successBody;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        if (this.mediaType != null) {
            response.setHeader("Content-Type", this.mediaType.toString());
        }
        if (StringUtils.hasLength(successBody)) {
            response.getWriter().write(successBody);
        }
    }
}
