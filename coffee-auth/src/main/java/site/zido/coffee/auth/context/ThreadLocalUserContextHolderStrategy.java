package site.zido.coffee.auth.context;

import org.springframework.util.Assert;

/**
 * @author zido
 */
public class ThreadLocalUserContextHolderStrategy
        implements UserContextHolderStrategy {
    private static final ThreadLocal<UserContext> CONTEXT_HOLDER = new ThreadLocal<>();

    @Override
    public void clearContext() {
        CONTEXT_HOLDER.remove();
    }

    @Override
    public UserContext getContext() {
        UserContext ctx = CONTEXT_HOLDER.get();
        if (ctx == null) {
            ctx = createEmptyContext();
            CONTEXT_HOLDER.set(ctx);
        }
        return ctx;
    }

    @Override
    public void setContext(UserContext context) {
        Assert.notNull(context, "Only non-null UserContext instances are permitted");
        CONTEXT_HOLDER.set(context);
    }

    @Override
    public UserContext createEmptyContext() {
        return new UserContextImpl();
    }
}
