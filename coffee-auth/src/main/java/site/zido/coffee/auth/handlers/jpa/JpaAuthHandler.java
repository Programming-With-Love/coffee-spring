package site.zido.coffee.auth.handlers.jpa;

import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.exceptions.AuthenticationException;
import site.zido.coffee.auth.handlers.AuthHandler;
import site.zido.coffee.auth.handlers.Authenticator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class JpaAuthHandler<T extends IUser> implements AuthHandler<T> {
    private Class<?> userClass;
    private List<Authenticator<T>> authenticators;

    public JpaAuthHandler(Class<?> userClass, List<Authenticator<T>> authenticators) {
        this.userClass = userClass;
        this.authenticators = authenticators;
    }

    public Class<?> getUserClass() {
        return userClass;
    }

    public void setUserClass(Class<?> userClass) {
        this.userClass = userClass;
    }

    @Override
    public T attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        for (Authenticator<T> authenticator : authenticators) {
            T auth = authenticator.auth(request);
            if (auth != null) {
                return auth;
            }
        }
        return null;
    }
}
