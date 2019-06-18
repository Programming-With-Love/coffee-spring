package site.zido.coffee.auth.entity;

import java.io.Serializable;

/**
 * user
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
    boolean enabled();
}
