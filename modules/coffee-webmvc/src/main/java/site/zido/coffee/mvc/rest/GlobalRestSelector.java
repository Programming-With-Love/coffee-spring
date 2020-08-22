package site.zido.coffee.mvc.rest;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 根据{@link EnableGlobalResult}注解确定是否使用全局rest json处理
 * <p>
 * 处理enable属性
 */
public class GlobalRestSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        Map<String, Object> maps = annotationMetadata.getAnnotationAttributes(EnableGlobalResult.class.getName());
        AnnotationAttributes enableGlobalResultAttributes = AnnotationAttributes.fromMap(maps);
        Assert.notNull(enableGlobalResultAttributes, "please use @EnableGlobalResult");
        boolean enable = enableGlobalResultAttributes.getBoolean("enable");
        if (!enable) {
            return new String[0];
        }
        return new String[]{GlobalRestConfiguration.class.getName()};
    }
}
