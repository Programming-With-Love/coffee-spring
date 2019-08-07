package site.zido.coffee.auth.entity;

import site.zido.coffee.auth.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

/**
 * 用户抽象接口
 *
 * @author zido
 */
public interface UserDetails extends Serializable {
    String DEFAULT_ROLE = "user";

    /**
     * 获取用户权限信息,不能返回null
     *
     * @return 权限集合
     */
    Collection<? extends GrantedAuthority> getAuthorities();

    /**
     * 获取用户名
     *
     * @return username
     */
    String getUsername();

    /**
     * 用户是否已过期，过期用户无法认证
     *
     * @return true/false
     */
    boolean isAccountNonExpired();

    /**
     * 用户是否已被锁定
     *
     * @return 如果未被锁定返回true, 否则返回false
     */
    boolean isAccountNonLocked();

    /**
     * 指示用户的凭据（密码）是否已过期。过期凭据阻止身份验证。
     *
     * @return 如果未过期返回true, 否则返回false
     */
    boolean isCredentialsNonExpired();

    /**
     * 角色
     *
     * @return role
     */
    default String role() {
        return DEFAULT_ROLE;
    }

    /**
     * 是否可用
     *
     * @return true/false
     */
    default boolean enabled() {
        return true;
    }
}
