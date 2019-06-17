package site.zido.coffee.auth.annotations;

import java.util.Collection;
import java.util.Set;

public class AuthVal {
    private boolean skip;
    private Collection<String> roles;
    private Collection<String> permissions;

    public Collection<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }
}
