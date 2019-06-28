package site.zido.coffee.auth.handlers;

public interface NoSuchUserHandler<T> {
    T handle(String nickName, String avatarUrl, Integer gender, String openId, String unionId);
}
