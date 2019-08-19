package site.zido.coffee.auth.config;

import org.springframework.core.type.AnnotationMetadata;

import java.util.logging.Filter;

public interface AuthenticationFilterBuilder {
    Filter createFilter(Class<?> userClass,
                        AnnotationMetadata metadata,
                        ObjectPostProcessor<Object> objectObjectPostProcessor);
}
