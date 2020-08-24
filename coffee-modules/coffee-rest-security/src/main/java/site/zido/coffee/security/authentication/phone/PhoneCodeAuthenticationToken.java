package site.zido.coffee.security.authentication.phone;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;

/**
 * @author zido
 */
public class PhoneCodeAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    public PhoneCodeAuthenticationToken(String phone, String code) {
        super(phone, code);
    }

    public PhoneCodeAuthenticationToken(String phone, String code, Collection<? extends GrantedAuthority> authorities) {
        super(phone, code, authorities);
    }
}
