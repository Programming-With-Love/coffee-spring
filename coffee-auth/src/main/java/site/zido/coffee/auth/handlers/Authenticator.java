package site.zido.coffee.auth.handlers;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.exceptions.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public interface Authenticator<T> {
    boolean prepare(Class<T> userClass, JpaRepository<T, ? extends Serializable> repository);

    T auth(HttpServletRequest request) throws AuthenticationException;

}
