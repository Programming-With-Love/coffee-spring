package site.zido.coffee.auth.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.Assert;
import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.common.rest.HttpResponseBodyFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestLoginSuccessHandler implements LoginSuccessHandler {
    private HttpResponseBodyFactory factory;
    private ObjectMapper mapper;

    public RestLoginSuccessHandler(HttpResponseBodyFactory factory) {
        this(factory, null);
    }

    public RestLoginSuccessHandler(HttpResponseBodyFactory factory, ObjectMapper mapper) {
        Assert.notNull(factory, "http response body factory can't be null");
        this.factory = factory;
        if (mapper != null) {
            this.mapper = mapper;
        } else {
            this.mapper = new ObjectMapper();
        }
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, IUser user) throws IOException, ServletException {
        Object result = factory.success(user);
        response.getWriter().write(mapper.writeValueAsString(result));
    }
}
