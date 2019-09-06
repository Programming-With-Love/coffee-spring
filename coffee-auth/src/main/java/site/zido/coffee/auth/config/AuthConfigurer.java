package site.zido.coffee.auth.config;

public interface AuthConfigurer<O, B extends AuthBuilder<O>> {
    void init(B builder) throws Exception;

    void configure(B builder) throws Exception;
}
