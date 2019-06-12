package site.zido.coffee.auth.core.authority;

import org.springframework.util.Assert;
import site.zido.coffee.auth.core.GrantedAuthority;

/**
 * @author zido
 */
public final class SimpleGrantedAuthority implements GrantedAuthority {
    private static final long serialVersionUID = -1027170363239788596L;
    private final String role;

    public SimpleGrantedAuthority(String role) {
        Assert.hasText(role, "A permission textual representation is required");
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
