package site.zido.coffee.auth.user;

public interface IUserPasswordService {
    IUser updatePassword(IUser user, String newPassword);
}
