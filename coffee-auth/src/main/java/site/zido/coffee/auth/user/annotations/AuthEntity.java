package site.zido.coffee.auth.user.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 标记该表为用户表(如果只有一个表可以不使用该注解,当有多个用户表时，需要使用该注解标记对应对应url),将用于登录
 *
 * @author zido
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthEntity {

    /**
     * 登陆入口url
     *
     * @return url path
     */
    @AliasFor("url")
    String value() default "";

    @AliasFor("value")
    String url() default "";

    /**
     * 指定管理的url,遵守ant url规范
     *
     * @return url
     */
    String[] baseUrl() default "";

    /**
     * 请求方法
     *
     * @return 允许认证请求方法
     */
    String[] method() default "POST";

    /**
     * 是否大小写敏感
     *
     * @return true/false
     */
    boolean caseSensitive() default true;

    /**
     * 可为当前实体配置默认角色集合，当用户角色关系简单时可以不必在数据库中查询用户角色
     * <p>
     * 此角色为最基础角色，当使用{@link AuthColumnRole}标记角色字段时，会在标记角色基础上加上此默认角色
     *
     * @return roles
     */
    String[] roles() default {};
}
