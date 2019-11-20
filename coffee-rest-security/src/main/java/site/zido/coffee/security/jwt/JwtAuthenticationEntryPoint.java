package site.zido.coffee.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import site.zido.coffee.common.CommonErrorCode;
import site.zido.coffee.common.rest.HttpResponseBodyFactory;
import site.zido.coffee.common.utils.ResponseUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zido
 */
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final String jsonError;
    private HttpResponseBodyFactory factory;

    public JwtAuthenticationEntryPoint(HttpResponseBodyFactory factory,
                                       ObjectMapper mapper) throws JsonProcessingException {
        jsonError = mapper.writeValueAsString(
                factory.error(CommonErrorCode.LIMIT,
                        "Sorry, You're not authorized to access this resource."));
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if (!response.isCommitted()) {
            ResponseUtils.json(response, HttpServletResponse.SC_UNAUTHORIZED, jsonError);
        }
    }
}
