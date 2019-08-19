package site.zido.coffee.auth.config;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class UserClassMetaData extends StandardAnnotationMetadata implements AnnotationMetadata {
    List<FieldMetaData> fieldMetaDataList = new ArrayList<>();
    private Field[] fields;
    //TODO user class metadata parse

    public UserClassMetaData(Class<?> introspectedClass) {
        super(introspectedClass);
        Field[] fields = introspectedClass.getDeclaredFields();
    }
}
