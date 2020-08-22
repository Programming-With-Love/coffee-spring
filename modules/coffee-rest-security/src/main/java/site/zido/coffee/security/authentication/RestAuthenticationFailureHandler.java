package site.zido.coffee.security.authentication;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zido
 */
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private MediaType mediaType;
    private String successBody;

    public RestAuthenticationFailureHandler() {
    }

    public RestAuthenticationFailureHandler(String successBody) {
        this(MediaType.APPLICATION_JSON, successBody);
    }

    public RestAuthenticationFailureHandler(MediaType mediaType, String successBody) {
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
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        if (this.mediaType != null) {
            response.setHeader("Content-Type", this.mediaType.toString());
        }
        if (StringUtils.hasLength(successBody)) {
            response.getWriter().write(successBody);
        }
    }
}
