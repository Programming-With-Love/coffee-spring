package site.zido.coffee.auth.entity;

import java.io.Serializable;
import java.util.Collection;

/**
 * user
 *
 * @author zido
 */
public interface IUser extends Serializable {

    Object key();

    /**
     * 获取用户角色 (不使用get,不参与序列化)
     *
     * @return 用户角色集合
     */
    default Collection<String> roles() {
        return null;
    }

    /**
     * 获取用户权限 (不使用get，不参与序列化)
     *
     * @return 用户权限集合
     */
    default Collection<String> permissions() {
        return null;
    }
}
