package site.zido.coffee.auth.authentication;

import org.springframework.data.jpa.repository.JpaRepository;
import site.zido.coffee.auth.user.IUser;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * 认证器
 *
 * @author zido
 */
public interface Authenticator {
    /**
     * 认证准备，创建认证器时的钩子
     *
     * @param userClass  用户类
     * @param repository jpa repository
     * @return 如果为true则表示支持此用户类，否则表示不支持此用户类
     */
    boolean prepare(Class<? extends IUser> userClass,
                    JpaRepository<? extends IUser, ? extends Serializable> repository);

    /**
     * 认证处理
     *
     * @param request 请求
     * @return 用户
     * @throws AbstractAuthenticationException 认证异常,需要注意此抛出会根据不同的类进行不同的逻辑处理
     */
    IUser auth(HttpServletRequest request) throws AbstractAuthenticationException;

}
