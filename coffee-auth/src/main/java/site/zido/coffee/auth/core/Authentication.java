package site.zido.coffee.auth.core;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;

/**
 * 认证主体，应当看作是用户的某一身份
 *
 * @author zido
 */
public interface Authentication extends Principal, Serializable {
    /**
     * 获取主体的权限，但是此方法在调用前应该询问是否已经经过验证{@link #isAuthenticated()}
     *
     * @return 授予委托人的权限，如果令牌未经过身份验证，则为空集合。返回结果不为null
     */
    Collection<? extends GrantedAuthority> getAuthorities();

    Object getCredentials();

    Object getDetails();

    Object getPrincipal();

    boolean isAuthenticated();

    void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException;
}
