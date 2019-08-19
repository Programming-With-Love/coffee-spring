package site.zido.coffee.auth.config;

import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;

public interface FieldMetaData extends AnnotatedTypeMetadata {
    String getFieldName();

    String getFieldType();
}
