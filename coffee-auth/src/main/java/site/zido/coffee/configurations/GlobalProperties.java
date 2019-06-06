package site.zido.coffee.configurations;

import site.zido.coffee.entity.EntityUser;

public class GlobalProperties {
    /**
     * 登录模式
     */
    private LoginMode loginMode;

    /**
     * 是否需要单点登录
     */
    private boolean sso = false;

    /**
     * 用户实体类
     */
    private Class<?> userClass = EntityUser.class;
}
