package site.zido.coffee.auth.handlers;

import java.util.List;

public class GenericAuthenticatorFacotory implements AuthenticatorFactory {
    private List<Class<? extends Authenticator>> authenticatorClasses;

    public GenericAuthenticatorFacotory() {

    }

    public GenericAuthenticatorFacotory(List<Class<? extends Authenticator>> authenticatorClasses) {
        this.authenticatorClasses = authenticatorClasses;
    }

    public void setAuthenticatorClasses(List<Class<? extends Authenticator>> authenticatorClasses) {
        this.authenticatorClasses = authenticatorClasses;
    }

    @Override
    public <T> List<Authenticator<T>> newChains(Class<?> javaType) {

    }
}
