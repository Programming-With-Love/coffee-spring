package site.zido.coffee.auth.context;

public class SecurityContextHolder {
    private static SecurityContextHolderStrategy strategy;
    private static int initialzeCount = 0;

    static {
        initialize();
    }

    private static void initialize() {
        strategy = new ThreadLocalSecurityContextHolderStrategy();
        initialzeCount++;
    }

    public static SecurityContextHolderStrategy getContextHolderStrategy() {
        return strategy;
    }

    public static SecurityContext createEmtpyContext() {
        return strategy.createEmptyContext();
    }

    public static void clearContext() {
        strategy.clearContext();
    }

    public static SecurityContext getContext() {
        return strategy.getContext();
    }

    public static void setContext(SecurityContext context) {
        strategy.setContext(context);
    }
}
