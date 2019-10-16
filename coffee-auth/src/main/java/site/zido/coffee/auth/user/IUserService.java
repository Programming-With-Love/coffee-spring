package site.zido.coffee.auth.user;

public interface IUserService<T extends IUser> {
    T loadUser(Object fieldValue);
}
