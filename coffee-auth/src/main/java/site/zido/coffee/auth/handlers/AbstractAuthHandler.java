package site.zido.coffee.auth.handlers;

import site.zido.coffee.auth.entity.IUser;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author zido
 */
public abstract class AbstractAuthHandler<T extends IUser, Key extends Serializable> implements AuthHandler<T, Key> {
    protected Class<T> userClass;
    protected Collection<Authenticator<T>> authenticators;

    public AbstractAuthHandler(Class<T> userClass, Collection<Authenticator<T>> authenticators) {
        this.userClass = userClass;
        this.authenticators = authenticators;
        init();
    }

    protected void init() {
        for (Authenticator<T> authenticator : authenticators) {
            authenticator.prepare(userClass);
        }
    }
}
