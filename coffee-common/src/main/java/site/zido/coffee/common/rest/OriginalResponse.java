package site.zido.coffee.common.rest;

import java.lang.annotation.*;

/**
 * 设置返回原响应内容，亦即不是用{@link Result} 进行数据包装
 *
 * @author zido
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface OriginalResponse {
}
