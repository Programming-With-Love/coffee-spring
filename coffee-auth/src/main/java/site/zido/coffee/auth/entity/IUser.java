package site.zido.coffee.auth.entity;

import site.zido.coffee.auth.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

/**
 * user
 *
 * @author zido
 */
public interface IUser extends Serializable {

    Object key();

    String getUsername();

    String getPassword();

    Collection<? extends GrantedAuthority> getAuthorities();

    boolean isAccountNonExpired();

    boolean isAccountNonLocked();

    boolean isCredentialsNonExpired();

    boolean isEnabled();
}
