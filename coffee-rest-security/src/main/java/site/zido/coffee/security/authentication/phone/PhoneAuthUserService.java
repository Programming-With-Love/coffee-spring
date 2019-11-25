package site.zido.coffee.security.authentication.phone;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;

/**
 * 手机号用户服务
 *
 * @author zido
 */
public interface PhoneAuthUserService {
    /**
     * 通过手机号获取用户
     *
     * @param phone 手机号
     * @return user
     * @throws AuthenticationException ex
     */
    UserDetails loadByPhone(String phone) throws AuthenticationException;
}
