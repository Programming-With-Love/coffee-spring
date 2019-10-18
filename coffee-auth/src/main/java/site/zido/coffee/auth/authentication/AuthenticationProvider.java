package site.zido.coffee.auth.authentication;

import site.zido.coffee.auth.core.Authentication;

/**
 * @author zido
 */
public interface AuthenticationProvider<T extends Authentication> {
    /**
     * 认证
     *
     * @param authentication 为认证的凭据
     * @return 已认证的凭据
     * @throws AbstractAuthenticationException ex
     */
    Authentication authenticate(T authentication)
            throws AbstractAuthenticationException;


    /**
     * 是否支持此认证类
     *
     * @param authentication authentication
     * @return true/false
     */
    boolean supports(Class<? extends Authentication> authentication);
}
