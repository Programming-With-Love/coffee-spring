package site.zido.coffee.auth.web.authentication;

import site.zido.coffee.auth.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 提供记住我服务
 *
 * @author zido
 */
public interface RememberMeService {
    /**
     * 自动登陆，在当前没有用户认证信息的时候会被调用
     *
     * @param request  request
     * @param response response
     * @return authentication
     */
    Authentication autoLogin(HttpServletRequest request, HttpServletResponse response);

    /**
     * 当登陆失败时会进行的登陆失败回调通知
     *
     * @param request  request
     * @param response response
     */
    void loginFail(HttpServletRequest request, HttpServletResponse response);

    /**
     * 登陆成功时会进行的登陆成功回调通知
     *
     * @param request                  request
     * @param response                 response
     * @param successfulAuthentication authentication
     */
    void loginSuccess(HttpServletRequest request, HttpServletResponse response,
                      Authentication successfulAuthentication);
}
