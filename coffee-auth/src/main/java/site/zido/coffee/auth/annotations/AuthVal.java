package site.zido.coffee.auth.annotations;

import java.util.Collection;
import java.util.Set;

/**
 * 关于权限判定的属性
 *
 * @author zido
 */
public class AuthVal {
    /**
     * 是否跳过
     */
    private boolean skip;
    /**
     * 角色信息
     */
    private Collection<String> roles;

    public Collection<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }
}
