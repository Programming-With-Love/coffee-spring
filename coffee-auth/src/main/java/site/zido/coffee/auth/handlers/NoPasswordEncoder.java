package site.zido.coffee.auth.handlers;

public class NoPasswordEncoder implements PasswordEncoder{
    @Override
    public String encode(String password) {
        return password;
    }

    @Override
    public boolean validate(String originPassword, String encodedPassword) {
        return originPassword != null && originPassword.equals(encodedPassword);
    }
}
