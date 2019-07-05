package site.zido.coffee.auth.handlers;

/**
 * @author zido
 */
public interface NoSuchUserHandler<T> {
    /**
     * 没有该用户的处理器
     * <p>
     * 一个经典的场景为，当用户未注册时，登陆自动注册
     * <p>
     * 所有参数来自微信服务器
     *
     * @param nickName  昵称
     * @param avatarUrl 头像url
     * @param gender    性别
     * @param openId    openId
     * @param unionId   unionId
     * @return user(如果实现了自动注册 ， 则需要返回用户 ， 否则返回null)
     */
    T handle(String nickName,
             String avatarUrl,
             Integer gender,
             String openId,
             String unionId);
}
