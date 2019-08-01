package site.zido.coffee.auth.authentication;

import site.zido.coffee.auth.core.Authentication;

public interface AuthenticationProvider {
    Authentication authenticate(Authentication authentication)
            throws AbstractAuthenticationException;


    boolean supports(Class<?> authentication);
}
