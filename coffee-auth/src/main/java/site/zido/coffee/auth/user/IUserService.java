package site.zido.coffee.auth.user;

import site.zido.coffee.auth.authentication.UsernameNotFoundException;

public interface IUserService {
    UserDetails findUserByUsername(String username) throws UsernameNotFoundException;
}
