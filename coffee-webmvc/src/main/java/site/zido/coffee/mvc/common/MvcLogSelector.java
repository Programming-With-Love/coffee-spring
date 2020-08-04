package site.zido.coffee.mvc.common;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 注解配置选择合适的日志输出
 * <p>
 * 未来计划包含监控日志选取支持，例如CAT等
 */
public class MvcLogSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(EnableRequestLogger.class.getName());
        AnnotationAttributes enableRequestLoggerAttributes = AnnotationAttributes.fromMap(attributes);
        Assert.notNull(enableRequestLoggerAttributes, "please use @EnableRequestLogger");
        boolean enable = enableRequestLoggerAttributes.getBoolean("enable");
        if (!enable)
            return new String[0];
        return new String[]{MvcLogConfiguration.class.getName()};
    }
}
