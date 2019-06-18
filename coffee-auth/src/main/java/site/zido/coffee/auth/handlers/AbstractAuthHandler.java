package site.zido.coffee.auth.handlers;

import site.zido.coffee.auth.entity.IUser;

import java.util.Collection;

/**
 * @author zido
 */
public abstract class AbstractAuthHandler implements AuthHandler {
    protected Class<? extends IUser> userClass;
    protected Collection<Authenticator<? extends IUser>> authenticators;

    public AbstractAuthHandler(Class<? extends IUser> userClass, Collection<Authenticator<? extends IUser>> authenticators) {
        this.userClass = userClass;
        this.authenticators = authenticators;
        init();
    }

    protected void init() {
        for (Authenticator<? extends IUser> authenticator : authenticators) {
            authenticator.prepare(userClass);
        }
    }
}
