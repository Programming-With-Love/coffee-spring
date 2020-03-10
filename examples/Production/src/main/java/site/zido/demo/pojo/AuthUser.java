package site.zido.demo.pojo;

import site.zido.demo.entity.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Data
public class AuthUser implements UserDetails {
    private static final Collection<? extends GrantedAuthority> vip =
            Arrays.asList(new SimpleGrantedAuthority("a"), new SimpleGrantedAuthority("b"));
    private User user;

    public AuthUser(User user) {
        this.user = user;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getVip() ? vip : Collections.EMPTY_LIST;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }
}
