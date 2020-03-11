package site.zido.coffee.extra.limiter;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用limiter,实际上这里还差一步，选择启用动态代理还是cglib,但是cglib暂时不了解，所以没有加入，默认使用动态代理
 *
 * @author zido
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AnnotationDrivenLimiterBeanProcessor.class)
public @interface EnableLimiter {
}
