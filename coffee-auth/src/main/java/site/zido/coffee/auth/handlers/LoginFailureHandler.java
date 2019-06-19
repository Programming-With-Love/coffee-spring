package site.zido.coffee.auth.handlers;

import site.zido.coffee.auth.exceptions.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface LoginFailureHandler {
    void onAuthenticationFailure(HttpServletRequest request,
                                 HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException;
}
