package site.zido.coffee.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author zido
 */
public class MobileCodeAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public MobileCodeAuthenticationToken(String mobile, String code) {
        super(mobile, code);
    }

    public MobileCodeAuthenticationToken(String mobile, String code, Collection<? extends GrantedAuthority> authorities) {
        super(mobile, code, authorities);
    }
}
