package site.zido.coffee.auth.context;

import site.zido.coffee.auth.core.Authentication;

import java.lang.annotation.*;

/**
 * 用于在mvc controller中获取当前用户角色对象
 * <p>
 * 等同于手动调用{@link Authentication#getPrincipal()}
 *
 * @author zido
 */
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
    /**
     * 当类型错误时，是否抛出异常{@link ClassCastException}
     * <p>
     * 当不抛出异常时，会为当前参数注入null
     *
     * @return true/false
     */
    boolean throwWhenTypeError() default false;

}
