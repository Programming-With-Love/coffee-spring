package site.zido.coffee.auth.context;

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

    public static UserContext get() {
        return strategy.getContext();
    }

    public static void set(UserContext context) {
        strategy.setContext(context);
    }
}
