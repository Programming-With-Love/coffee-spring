package site.zido.coffee.auth.core;

import java.io.Serializable;

/**
 * 表示授予{@link Authentication}对象的权限。
 *
 * @author zido
 */
public interface GrantedAuthority extends Serializable {
    /**
     * 返回权限字符串
     *
     * @return 权限对应的字符串
     */
    String getAuthority();
}
