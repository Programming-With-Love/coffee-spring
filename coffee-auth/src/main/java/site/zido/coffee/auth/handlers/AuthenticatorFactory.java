package site.zido.coffee.auth.handlers;

import java.util.List;

public interface AuthenticatorFactory {
    <T> List<Authenticator<T>> newChains(Class<?> javaType);
}
