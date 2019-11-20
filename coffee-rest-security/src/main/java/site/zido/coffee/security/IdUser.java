package site.zido.coffee.security;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author zido
 */
public interface IdUser<T extends Serializable> {
    T getId();

    Collection<? extends GrantedAuthority> getAuthorities();
}
