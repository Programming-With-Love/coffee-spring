package site.zido.coffee.auth.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.Assert;
import site.zido.coffee.auth.authentication.AbstractAuthenticationException;
import site.zido.coffee.auth.authentication.InternalAuthenticationException;
import site.zido.coffee.auth.utils.ResponseUtils;
import site.zido.coffee.common.CommonErrorCode;
import site.zido.coffee.common.rest.HttpResponseBodyFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zido
 */
public class RestLoginFailureHandler implements LoginFailureHandler {
    private HttpResponseBodyFactory factory;
    private ObjectMapper mapper;
    private final String UNKNOWN_MESSAGE;

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
        try {
            UNKNOWN_MESSAGE = this.mapper
                    .writeValueAsString(
                            factory.error(
                                    CommonErrorCode.UNKNOWN, "未知登陆的错误", null));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AbstractAuthenticationException exception)
            throws IOException {
        //内部异常应该由后端开发者根据日志进行debug
        if (exception instanceof InternalAuthenticationException) {
            ResponseUtils.json(response, UNKNOWN_MESSAGE);
            return;
        }
        //其他异常应该给前端提供相应的异常信息，以帮助前端进行相应的改善
        ResponseUtils.json(response, mapper.writeValueAsString(
                factory.error(exception, null)));
    }
}
