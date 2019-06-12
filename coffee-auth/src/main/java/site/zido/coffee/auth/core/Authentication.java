package site.zido.coffee.auth.core;

import java.util.Collection;

public interface Authentication {
    Collection<? extends GrantedAuthority> getAuthorities();

    Object getPrincipal();

    boolean isAuthenticated();

    void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException;
}
