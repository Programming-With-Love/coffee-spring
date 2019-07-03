package site.zido.coffee.auth.entity;

/**
 * 匿名用户
 */
public class AnonymousUser implements IUser {
    public static final String ROLE_ANONYMOUS = "ANONYMOUS";

    @Override
    public String role() {
        return ROLE_ANONYMOUS;
    }
}
