package site.zido.coffee.auth.core.authentication;

import site.zido.coffee.auth.core.Authentication;
import site.zido.coffee.auth.core.CredentialsContainer;
import site.zido.coffee.auth.core.GrantedAuthority;
import site.zido.coffee.auth.entity.IUser;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public abstract class AbstractAuthentication implements Authentication, CredentialsContainer {
    private final Collection<GrantedAuthority> authorities;

    private Object details;

    private boolean authenticated = false;

    public AbstractAuthentication(Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null) {
            this.authorities = Collections.emptyList();
            return;
        }
        for (GrantedAuthority authority : authorities) {
            if (authority == null) {
                throw new IllegalArgumentException(
                        "Authorities collection cannot contain any null elements");
            }
        }

        ArrayList<GrantedAuthority> temp = new ArrayList<>(authorities.size());
        temp.addAll(authorities);
        this.authorities = Collections.unmodifiableList(temp);
    }

    @Override
    public String getName(){
        if(this.getPrincipal() instanceof IUser){
            return ((IUser)this.getPrincipal()).getUsername();
        }
        if(this.getPrincipal() instanceof Principal){
            return ((Principal)this.getPrincipal()).getName();
        }
        return (this.getPrincipal() == null)? "":this.getPrincipal().toString();
    }
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    @Override
    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    @Override
    public void eraseCredentials() {
        eraseSecret(getCredentials());
        eraseSecret(getPrincipal());
        eraseSecret(details);
    }

    private void eraseSecret(Object secret) {
        if (secret instanceof CredentialsContainer) {
            ((CredentialsContainer) secret).eraseCredentials();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractAuthentication)) {
            return false;
        }

        AbstractAuthentication test = (AbstractAuthentication) obj;

        if (!authorities.equals(test.authorities)) {
            return false;
        }

        if ((this.details == null) && (test.getDetails() != null)) {
            return false;
        }

        if ((this.details != null) && (test.getDetails() == null)) {
            return false;
        }

        if ((this.details != null) && (!this.details.equals(test.getDetails()))) {
            return false;
        }

        if ((this.getCredentials() == null) && (test.getCredentials() != null)) {
            return false;
        }

        if ((this.getCredentials() != null)
                && !this.getCredentials().equals(test.getCredentials())) {
            return false;
        }

        if (this.getPrincipal() == null && test.getPrincipal() != null) {
            return false;
        }

        if (this.getPrincipal() != null
                && !this.getPrincipal().equals(test.getPrincipal())) {
            return false;
        }

        return this.isAuthenticated() == test.isAuthenticated();
    }

    @Override
    public int hashCode() {
        int code = 31;

        for (GrantedAuthority authority : authorities) {
            code ^= authority.hashCode();
        }

        if (this.getPrincipal() != null) {
            code ^= this.getPrincipal().hashCode();
        }

        if (this.getCredentials() != null) {
            code ^= this.getCredentials().hashCode();
        }

        if (this.getDetails() != null) {
            code ^= this.getDetails().hashCode();
        }

        if (this.isAuthenticated()) {
            code ^= -37;
        }

        return code;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": ");
        sb.append("Principal: ").append(this.getPrincipal()).append("; ");
        sb.append("Credentials: [PROTECTED]; ");
        sb.append("Authenticated: ").append(this.isAuthenticated()).append("; ");
        sb.append("Details: ").append(this.getDetails()).append("; ");

        if (!authorities.isEmpty()) {
            sb.append("Granted Authorities: ");

            int i = 0;
            for (GrantedAuthority authority : authorities) {
                if (i++ > 0) {
                    sb.append(", ");
                }

                sb.append(authority);
            }
        } else {
            sb.append("Not granted any authorities");
        }

        return sb.toString();
    }
}
