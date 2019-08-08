package site.zido.coffee.auth.user;

public interface IUserPasswordService {
    UserDetails updatePassword(UserDetails user, String newPassword);
}
