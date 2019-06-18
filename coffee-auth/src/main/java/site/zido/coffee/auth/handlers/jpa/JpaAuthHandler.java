package site.zido.coffee.auth.handlers.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;
import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.handlers.AuthHandler;

import java.io.Serializable;

public class JpaAuthHandler implements AuthHandler {
    private Class<?> userClass;
    private JpaRepository userRepository;

    public JpaAuthHandler(Class<?> userClass, JpaRepository userRepository) {
        this.userClass = userClass;
        this.userRepository = userRepository;
        init();
    }

    private void init() {

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
    @SuppressWarnings("unchecked")
    public IUser getUserByKey(Object key) {
        return (IUser) userRepository.findOne((Serializable) key);
    }
}
