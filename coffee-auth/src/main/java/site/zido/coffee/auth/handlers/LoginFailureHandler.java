package site.zido.coffee.auth.handlers;

import site.zido.coffee.auth.exceptions.AbstractAuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zido
 */
public interface LoginFailureHandler {
    /**
     * 认证失败时的回调处理
     *
     * @param request   request
     * @param response  response
     * @param exception exception,可能为{@link site.zido.coffee.auth.exceptions.InternalAuthenticationException}
     *                  ,相关日志已经完成打印.
     * @throws IOException      io exception
     * @throws ServletException servlet exception
     */
    void onAuthenticationFailure(HttpServletRequest request,
                                 HttpServletResponse response, AbstractAuthenticationException exception)
            throws IOException, ServletException;
}
