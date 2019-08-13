package site.zido.coffee.auth.user;

import site.zido.coffee.auth.authentication.UsernameNotFoundException;

public interface IUserService {
    UserDetails findUserByKey(String username) throws UsernameNotFoundException;
}
