package site.zido.coffee.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.handlers.Authenticator;

import java.io.Serializable;
import java.util.List;

/**
 * @author zido
 */
public interface AuthenticatorFactory {
    <T extends IUser> List<Authenticator<T>> newChains(Class<T> javaType,
                                                       JpaRepository<T, ? extends Serializable> repository);
}
