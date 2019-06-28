package site.zido.coffee.auth.handlers;

public interface PasswordEncoder {
    String encode(String password);

    boolean validate(String originPassword, String encodedPassword);
}
