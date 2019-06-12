package site.zido.coffee.auth.core.authentication;

import site.zido.coffee.auth.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author zido
 */
public class UsernamePasswordAuthentication extends AbstractAuthentication {
    private static final long serialVersionUID = -3895933457642601075L;
    private final String username;
    private String password;

    /**
     * 无法获得任何权限，只是一个登录用户
     * @param username username
     * @param password password
     */
    public UsernamePasswordAuthentication(String username, String password) {
        super(null);
        this.username = username;
        this.password = password;
        setAuthenticated(false);
    }

    /**
     * 授予相应的权限
     * @param username username
     * @param password password
     * @param authorities 权限集合
     */
    public UsernamePasswordAuthentication(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.username = username;
        this.password = password;
        super.setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    /**
     * 禁止二次认证
     */
    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }

        super.setAuthenticated(false);
    }


    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        password = null;
    }
}
