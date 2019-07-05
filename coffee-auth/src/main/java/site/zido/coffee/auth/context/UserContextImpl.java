package site.zido.coffee.auth.context;

import site.zido.coffee.auth.entity.IUser;

/**
 * @author zido
 */
public class UserContextImpl implements UserContext {
    private static final long serialVersionUID = -1L;

    private IUser user;

    public UserContextImpl() {
    }

    public UserContextImpl(IUser user) {
        this.user = user;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public void setUser(IUser user) {
        this.user = user;
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof UserContextImpl) {
            UserContextImpl test = (UserContextImpl) o;
            if ((this.getUser() == null) && (test.getUser() == null)) {
                return true;
            }
            return (this.getUser() != null) && (test.getUser() != null)
                    && this.getUser().equals(test.getUser());
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (this.getUser() == null) {
            return -1;
        } else {
            return this.getUser().hashCode();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        if (this.user == null) {
            sb.append(": Null user");
        } else {
            sb.append(": Authentication: ").append(this.user);
        }
        return sb.toString();
    }
}
