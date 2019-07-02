package site.zido.coffee.auth.entity;

import java.io.Serializable;

/**
 * 用户抽象接口
 *
 * @author zido
 */
public interface IUser extends Serializable {

    /**
     * 角色
     *
     * @return role
     */
    default String role() {
        return null;
    }

    /**
     * 是否可用
     *
     * @return true/false
     */
    default boolean enabled() {
        return true;
    }
}
