package site.zido.coffee.auth.entity;

import java.io.Serializable;
import java.util.Collection;

/**
 * user
 *
 * @author zido
 */
public interface IUser extends Serializable {

    /**
     * 角色
     *
     * @return roles
     */
    Collection<String> roles();

    /**
     * 是否可用
     *
     * @return true/false
     */
    boolean enabled();
}
