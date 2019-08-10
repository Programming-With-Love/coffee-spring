package site.zido.coffee.auth.authentication;

import site.zido.coffee.auth.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证令牌工厂
 *
 * @author zido
 */
public interface AuthenticationTokenFactory {

    /**
     * 通过请求创建认证令牌
     *
     * @param request  request
     * @param response response
     * @return authentication token
     */
    Authentication createToken(HttpServletRequest request, HttpServletResponse response);
}
