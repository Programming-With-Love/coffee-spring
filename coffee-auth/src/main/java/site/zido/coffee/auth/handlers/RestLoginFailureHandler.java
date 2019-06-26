package site.zido.coffee.auth.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.Assert;
import site.zido.coffee.auth.exceptions.AuthenticationException;
import site.zido.coffee.common.rest.HttpResponseBodyFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestLoginFailureHandler implements LoginFailureHandler {
    private HttpResponseBodyFactory factory;
    private ObjectMapper mapper;

    public RestLoginFailureHandler(HttpResponseBodyFactory factory) {
        this(factory, null);
    }

    public RestLoginFailureHandler(HttpResponseBodyFactory factory, ObjectMapper mapper) {
        Assert.notNull(factory, "http response body factory can't be null");
        this.factory = factory;
        if (mapper != null) {
            this.mapper = mapper;
        } else {
            this.mapper = new ObjectMapper();
        }
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
    }
}
