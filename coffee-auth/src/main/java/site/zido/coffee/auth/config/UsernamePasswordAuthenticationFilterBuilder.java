package site.zido.coffee.auth.config;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import site.zido.coffee.auth.authentication.UsernamePasswordAuthenticationToken;
import site.zido.coffee.auth.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.logging.Filter;

public class UsernamePasswordAuthenticationFilterBuilder implements AuthenticationFilterBuilder {

    @Override
    public Filter createFilter(Class<?> userClass, AnnotationMetadata metadata, ObjectPostProcessor<Object> objectObjectPostProcessor) {

        return null;
    }
}
