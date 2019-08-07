package site.zido.coffee.auth.context;

import site.zido.coffee.auth.core.Authentication;

/**
 * @author zido
 */
public class UserContextImpl implements UserContext {
    private static final long serialVersionUID = -1L;

    private Authentication authentication;

    public UserContextImpl() {
    }

    public UserContextImpl(Authentication authentication) {
        this.authentication = authentication;
    }

    @Override
    public Authentication getAuthentication() {
        return authentication;
    }

    @Override
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof UserContextImpl) {
            UserContextImpl test = (UserContextImpl) o;
            if ((this.getAuthentication() == null) && (test.getAuthentication() == null)) {
                return true;
            }
            return (this.getAuthentication() != null) && (test.getAuthentication() != null)
                    && this.getAuthentication().equals(test.getAuthentication());
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (this.getAuthentication() == null) {
            return -1;
        } else {
            return this.getAuthentication().hashCode();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        if (this.authentication == null) {
            sb.append(": Null Authentication");
        } else {
            sb.append(": Authentication: ").append(this.authentication);
        }
        return sb.toString();
    }
}
