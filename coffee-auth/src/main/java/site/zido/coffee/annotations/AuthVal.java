package site.zido.coffee.annotations;

import java.util.Collection;
import java.util.Set;

public class AuthVal {
    private Collection<String> roles;
    private Collection<String> permissions;

    public Collection<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Collection<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

}
