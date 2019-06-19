package site.zido.coffee.auth.handlers.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;
import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.exceptions.AuthenticationException;
import site.zido.coffee.auth.handlers.AuthHandler;
import site.zido.coffee.auth.handlers.Authenticator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JpaAuthHandler<T extends IUser, Key extends Serializable> implements AuthHandler<T, Key> {
    private Class<?> userClass;
    private JpaRepository<T, Key> userRepository;
    private List<Authenticator<T>> authenticators;

    public JpaAuthHandler(Class<?> userClass, JpaRepository<T, Key> userRepository) {
        this.userClass = userClass;
        this.userRepository = userRepository;
        init();
    }

    private void init() {
        authenticators = new ArrayList<>();

    }

    public Class<?> getUserClass() {
        return userClass;
    }

    public void setUserClass(Class<?> userClass) {
        this.userClass = userClass;
    }

    public Repository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(JpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public T getUserByKey(Key key) {
        return userRepository.findOne(key);
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
