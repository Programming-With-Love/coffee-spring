package site.zido.coffee.auth.core.authority;

import org.springframework.util.Assert;
import site.zido.coffee.auth.Constants;
import site.zido.coffee.auth.core.GrantedAuthority;

/**
 * 简单的权限表示实现
 *
 * @author zido
 */
public class SimpleGrantedAuthority implements GrantedAuthority {
    private static final long serialVersionUID = Constants.COFFEE_AUTH_VERSION;

    private final String role;

    public SimpleGrantedAuthority(String role) {
        Assert.hasText(role, "role text can't be null or empty");
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return role;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof SimpleGrantedAuthority) {
            return role.equals(((SimpleGrantedAuthority) obj).role);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.role.hashCode();
    }

    @Override
    public String toString() {
        return this.role;
    }
}
