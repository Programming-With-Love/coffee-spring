package site.zido.coffee.auth.core;

import java.io.Serializable;

public interface GrantedAuthority extends Serializable {
    String getAuthority();
}
