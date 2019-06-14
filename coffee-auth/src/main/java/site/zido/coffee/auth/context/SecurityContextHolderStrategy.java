package site.zido.coffee.auth.context;

public interface SecurityContextHolderStrategy {
    void clearContext();

    SecurityContext getContext();

    void setContext(SecurityContext context);

    SecurityContext createEmptyContext();
}
