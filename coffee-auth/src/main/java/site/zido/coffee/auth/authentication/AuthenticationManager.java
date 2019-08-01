package site.zido.coffee.auth.authentication;

import site.zido.coffee.auth.core.Authentication;

import javax.naming.AuthenticationException;

/**
 * 认证处理器
 *
 * @author zido
 */
public interface AuthenticationManager {
    /**
     * 尝试对传递的{@link Authentication}对象进行身份验证，如果成功则返回
     * 完全填充的<code> Authentication </ code>对象（包括授予的权限）*。
     *
     * @param authentication 认证请求
     * @return 完全认证对象
     * @throws AuthenticationException 如果认证失败则抛出此异常
     */
    Authentication authenticate(Authentication authentication)
            throws AuthenticationException;
}
