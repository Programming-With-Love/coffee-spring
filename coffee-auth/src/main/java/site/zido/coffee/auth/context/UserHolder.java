package site.zido.coffee.auth.context;

import site.zido.coffee.auth.entity.IUser;

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
