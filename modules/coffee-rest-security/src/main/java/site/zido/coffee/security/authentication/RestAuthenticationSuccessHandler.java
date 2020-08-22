package site.zido.coffee.security.authentication;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zido
 */
public class RestAuthenticationSuccessHandler implements
        AuthenticationSuccessHandler {
    private MediaType mediaType;
    private String successBody;

    public RestAuthenticationSuccessHandler() {
    }

    public RestAuthenticationSuccessHandler(String successBody) {
        this(MediaType.APPLICATION_JSON, successBody);
    }

    public RestAuthenticationSuccessHandler(MediaType mediaType, String successBody) {
        this.mediaType = mediaType;
        this.successBody = successBody;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (this.mediaType != null) {
            response.setHeader("Content-Type", this.mediaType.toString());
        }
        if (StringUtils.hasLength(successBody)) {
            response.getWriter().write(successBody);
        }
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public void setSuccessBody(String successBody) {
        this.successBody = successBody;
    }
}
