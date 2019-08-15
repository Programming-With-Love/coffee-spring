package site.zido.coffee.auth.user;

/**
 * 用于标记用户类被包装过
 *
 * @author zido
 */
public interface WrapperUser {

    /**
     * 获取原用户类型
     *
     * @return user class
     */
    Class<?> getUserClass();
}
