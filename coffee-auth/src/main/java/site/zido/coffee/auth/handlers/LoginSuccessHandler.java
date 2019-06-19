package site.zido.coffee.auth.handlers;

import site.zido.coffee.auth.entity.IUser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface LoginSuccessHandler {
    void onAuthenticationSuccess(HttpServletRequest request,
                                 HttpServletResponse response, IUser user)
            throws IOException, ServletException;
}
