package site.zido.coffee.auth.context;

import site.zido.coffee.auth.entity.IUser;

import java.io.Serializable;

public interface UserContext extends Serializable {

    IUser getUser();

    void setUser(IUser user);
}
