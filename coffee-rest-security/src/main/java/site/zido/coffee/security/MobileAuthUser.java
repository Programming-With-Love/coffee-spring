package site.zido.coffee.security;

import java.io.Serializable;

public interface MobileAuthUser<T extends Serializable> extends IdUser<T> {
    String getMobile();
}
