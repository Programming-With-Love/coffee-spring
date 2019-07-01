package site.zido.coffee.auth.handlers;

import org.springframework.data.jpa.repository.JpaRepository;
import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.exceptions.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public interface Authenticator {
    boolean prepare(Class<? extends IUser> userClass,
                    JpaRepository<? extends IUser, ? extends Serializable> repository);

    IUser auth(HttpServletRequest request) throws AuthenticationException;

}
