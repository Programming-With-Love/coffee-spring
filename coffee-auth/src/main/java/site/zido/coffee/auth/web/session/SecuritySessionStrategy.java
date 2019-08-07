package site.zido.coffee.auth.web.session;

import site.zido.coffee.auth.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证的session相关支持
 *
 * @author zido
 */
public interface SecuritySessionStrategy {
    /**
     * 发生新身份验证时执行与Http会话相关的功能。
     *
     * @param authentication authentication
     * @param request        request
     * @param response       response
     * @throws SessionAuthenticationException ex
     */
    void onAuthentication(Authentication authentication, HttpServletRequest request,
                          HttpServletResponse response) throws SessionAuthenticationException;
}
