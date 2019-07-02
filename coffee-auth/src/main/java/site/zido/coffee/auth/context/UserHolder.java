package site.zido.coffee.auth.context;

import site.zido.coffee.auth.entity.IUser;

/**
 * 全局用户保持,线程级
 *
 * @author zido
 */
public class UserHolder {
    private static ThreadLocal<IUser> local = new ThreadLocal<>();

    public static void clearContext() {
        local.remove();
    }

    public static IUser get() {
        return local.get();
    }

    public static void set(IUser user) {
        local.set(user);
    }
}
