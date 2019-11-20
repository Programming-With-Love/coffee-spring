package site.zido.coffee.security.authentication;

import java.io.Serializable;

public interface MobileAuthUser<T extends Serializable> extends IdUser<T> {
    String getMobile();
}
