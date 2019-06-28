package site.zido.coffee.auth.handlers;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import site.zido.coffee.auth.entity.IUser;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public interface Authenticator<T, ID extends Serializable> {
    boolean prepare(Class<T> userClass, JpaRepository<T, ID> repository);

    T auth(HttpServletRequest request);

}
