package site.zido.coffee.security.authentication.phone;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author zido
 */
public class PhoneCodeAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public PhoneCodeAuthenticationToken(String phone, String code) {
        super(phone, code);
    }

    public PhoneCodeAuthenticationToken(String phone, String code, Collection<? extends GrantedAuthority> authorities) {
        super(phone, code, authorities);
    }
}
