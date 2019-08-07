package site.zido.coffee.auth.context;

import site.zido.coffee.auth.core.Authentication;
import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.entity.UserDetails;

import java.io.Serializable;

public interface UserContext extends Serializable {

    Authentication getAuthentication();

    void setAuthentication(Authentication authentication);
}
