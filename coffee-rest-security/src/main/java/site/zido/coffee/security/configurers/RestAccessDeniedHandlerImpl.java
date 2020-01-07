package site.zido.coffee.security.configurers;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestAccessDeniedHandlerImpl implements AccessDeniedHandler {
    private MediaType mediaType;
    private String successBody;

    public RestAccessDeniedHandlerImpl() {
    }

    public RestAccessDeniedHandlerImpl(String successBody) {
        this(MediaType.APPLICATION_JSON, successBody);
    }

    public RestAccessDeniedHandlerImpl(MediaType mediaType, String successBody) {
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
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        if (this.mediaType != null) {
            response.setHeader("Content-Type", this.mediaType.toString());
        }
        if (StringUtils.hasLength(successBody)) {
            response.getWriter().write(successBody);
        }
    }
}
