package site.zido.coffee.mvc.common;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 注解配置选择合适的日志输出
 *
 * 未来计划包含监控日志选取支持，例如CAT等
 */
public class MvcLogSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {

        return new String[0];
    }
}
