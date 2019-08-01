package site.zido.coffee.auth.authentication;

import site.zido.coffee.auth.entity.IUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证处理器，用于协调各个认证器的工作
 *
 * @author zido
 */
public interface AuthHandler {

    /**
     * 认证处理
     *
     * @param request  request
     * @param response response
     * @return 用户
     * @throws AbstractAuthenticationException ex
     */
    IUser attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AbstractAuthenticationException;
}
