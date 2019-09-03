package site.zido.coffee.auth.authentication.logout;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.Assert;
import site.zido.coffee.auth.core.Authentication;
import site.zido.coffee.auth.utils.ResponseUtils;
import site.zido.coffee.common.rest.HttpResponseBodyFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestLogoutSuccessHandler implements LogoutSuccessHandler {
    private HttpResponseBodyFactory factory;
    private ObjectMapper mapper;

    public RestLogoutSuccessHandler(HttpResponseBodyFactory factory) {
        this(factory, null);
    }

    public RestLogoutSuccessHandler(HttpResponseBodyFactory factory, ObjectMapper mapper) {
        Assert.notNull(factory, "http response body factory can't be null");
        this.factory = factory;
        if (mapper != null) {
            this.mapper = mapper;
        } else {
            this.mapper = new ObjectMapper();
        }
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Object result = factory.success(authentication);
        ResponseUtils.json(response,
                mapper.writeValueAsString(result));
    }
}
