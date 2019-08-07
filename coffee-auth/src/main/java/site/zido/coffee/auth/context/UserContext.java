package site.zido.coffee.auth.context;

import site.zido.coffee.auth.core.Authentication;

import java.io.Serializable;

public interface UserContext extends Serializable {

    Authentication getAuthentication();

    void setAuthentication(Authentication authentication);
}
