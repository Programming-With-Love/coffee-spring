package site.zido.coffee.auth.handlers;

import site.zido.coffee.auth.core.Authentication;
import site.zido.coffee.auth.entity.IUser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证成功处理器
 *
 * @author zido
 */
public interface LoginSuccessHandler {
    /**
     * 当认证成功时的相应处理
     *
     * @param request        request
     * @param response       response
     * @param authentication authentication
     * @throws IOException      ex
     * @throws ServletException ex
     */
    void onAuthenticationSuccess(HttpServletRequest request,
                                 HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException;
}
