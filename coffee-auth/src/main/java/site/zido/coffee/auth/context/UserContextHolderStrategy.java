package site.zido.coffee.auth.context;

/**
 * @author zido
 */
public interface UserContextHolderStrategy {
    void clearContext();

    UserContext getContext();

    void setContext(UserContext context);

    UserContext createEmptyContext();
}
