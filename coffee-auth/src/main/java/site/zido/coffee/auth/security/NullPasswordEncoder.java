package site.zido.coffee.auth.security;

/**
 * @author zido
 */
public class NullPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(String password) {
        return password;
    }

    @Override
    public boolean validate(String originPassword, String encodedPassword) {
        return originPassword != null && originPassword.equals(encodedPassword);
    }
}
