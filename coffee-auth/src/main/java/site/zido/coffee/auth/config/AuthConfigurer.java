package site.zido.coffee.auth.config;

/**
 * @author zido
 */
public interface AuthConfigurer<O, B extends AuthBuilder<O>> {
    void init(B builder) throws Exception;

    void configure(B builder) throws Exception;
}
