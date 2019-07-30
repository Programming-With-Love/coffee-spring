package site.zido.coffee.auth.entity;

import java.io.Serializable;

/**
 * 用户抽象接口
 *
 * @author zido
 */
public interface IUser extends Serializable {
    String DEFAULT_ROLE = "user";

    /**
     * 角色
     *
     * @return role
     */
    default String role() {
        return DEFAULT_ROLE;
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
