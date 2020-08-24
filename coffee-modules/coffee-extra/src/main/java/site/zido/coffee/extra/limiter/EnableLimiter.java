package site.zido.coffee.extra.limiter;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import java.lang.annotation.*;

/**
 * 启用limiter,与{@link org.springframework.cache.annotation.EnableCaching}类似
 * 可以选择是否使用cglib
 *
 * @author zido
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LimiterConfigurationSelector.class)
public @interface EnableLimiter {

    /**
     * 是否强制使用cglib代理
     *
     * @return true/false
     */
    boolean proxyTargetClass() default false;

    /**
     * 选择代理模式
     */
    AdviceMode mode() default AdviceMode.PROXY;

    /**
     * 代理顺序
     */
    int order() default Ordered.LOWEST_PRECEDENCE;
}
