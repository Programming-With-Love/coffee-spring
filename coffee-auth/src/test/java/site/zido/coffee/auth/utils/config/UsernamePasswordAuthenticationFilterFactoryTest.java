package site.zido.coffee.auth.utils.config;

import example.User;
import org.junit.Test;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import javax.persistence.Id;
import java.io.IOException;
import java.util.Set;

public class UsernamePasswordAuthenticationFilterFactoryTest {
    @Test
    public void testGetMetaData() throws IOException {
        MetadataReaderFactory factory = new SimpleMetadataReaderFactory(getClass().getClassLoader());
        MetadataReader reader = factory.getMetadataReader(User.class.getName());
        Set<MethodMetadata> annotatedMethods = reader.getAnnotationMetadata().getAnnotatedMethods(Id.class.getName());
        System.out.println(annotatedMethods);
    }
}
