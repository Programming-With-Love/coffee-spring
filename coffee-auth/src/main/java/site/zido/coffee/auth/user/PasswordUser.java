package site.zido.coffee.auth.user;

import site.zido.coffee.auth.core.CredentialsContainer;
import site.zido.coffee.auth.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author zido
 */
public class PasswordUser extends User implements CredentialsContainer {
    private String password;

    public PasswordUser(Object userKey, String password, Collection<? extends GrantedAuthority> authorities) {
        super(userKey, authorities);
        this.password = password;
    }

    public PasswordUser(Object userKey, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(userKey, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }
}
