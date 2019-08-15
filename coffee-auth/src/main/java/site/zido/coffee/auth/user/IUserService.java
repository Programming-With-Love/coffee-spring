package site.zido.coffee.auth.user;

public interface IUserService {
    Object loadUser(Object fieldValue, String fieldName, Class<?> userClass);
}
