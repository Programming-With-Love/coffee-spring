package site.zido.coffee.security;

import org.springframework.security.core.AuthenticationException;

import java.io.Serializable;

public interface MobileAuthUserService<T extends Serializable> {
    MobileAuthUser<T> loadByMobile(String mobile) throws AuthenticationException;
}
