package site.zido.coffee.auth.handlers;

import org.springframework.data.jpa.repository.JpaRepository;
import site.zido.coffee.auth.AuthenticatorFactory;
import site.zido.coffee.auth.entity.IUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DefaultAuthenticatorFactory implements AuthenticatorFactory {

    @Override
    public <T extends IUser> List<Authenticator<T>> newChains(Class<T> javaType, JpaRepository<T, ? extends Serializable> repository) {
        List<Authenticator<T>> results = new ArrayList<>();
        //TODO auto wire authenticator properties
        UsernamePasswordAuthenticator<T> usernamePasswordAuthenticator
                = new UsernamePasswordAuthenticator<>();
        if (usernamePasswordAuthenticator.prepare(javaType, repository)) {
            results.add(usernamePasswordAuthenticator);
        }
        WechatAuthenticator<T> wechatAuthenticator
                = new WechatAuthenticator<>();
        if(wechatAuthenticator.prepare(javaType,repository)){
            results.add(wechatAuthenticator);
        }
        return results;
    }

}
