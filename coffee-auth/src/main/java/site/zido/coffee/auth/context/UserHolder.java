package site.zido.coffee.auth.context;

import site.zido.coffee.auth.entity.IUser;

/**
 * 全局用户保持,线程级
 *
 * @author zido
 */
public class UserHolder {
    public static UserContextHolderStrategy strategy = new ThreadLocalUserContextHolderStrategy();

    public static void clearContext() {
        strategy.clearContext();
    }

    public static IUser get() {
        return strategy.getContext().getUser();
    }

    public static void set(UserContext context) {
        strategy.setContext(context);
    }
}
