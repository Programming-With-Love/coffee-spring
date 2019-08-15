package site.zido.coffee.auth.utils;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 属性注入工具,内置缓存，尽量只有高频操作使用此工具
 *
 * @author zido
 */
public class CachedFieldUtils {
    private static final Map<PrimKey, Method> SETTER_CACHE = new ConcurrentHashMap<>(6);
    private static final Map<PrimKey, Method> GETTER_CACHE = new ConcurrentHashMap<>(6);

    private static final class PrimKey {
        private Class<?> clazz;
        private String fieldName;

        private PrimKey(Class<?> clazz, String fieldName) {
            this.clazz = clazz;
            this.fieldName = fieldName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            PrimKey primKey = (PrimKey) o;
            return Objects.equals(clazz, primKey.clazz) &&
                    Objects.equals(fieldName, primKey.fieldName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clazz, fieldName);
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public void setClazz(Class<?> clazz) {
            this.clazz = clazz;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }
    }

    /**
     * 直接注入field
     *
     * @param field field
     * @param obj   target object
     * @param value value
     */
    public static void injectField(Field field, Object obj, Object value) {
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, obj, value);
    }

    /**
     * 通过setter方法注入field
     *
     * @param field field
     * @param obj   target object
     * @param value value
     */
    public static void injectFieldBySetter(Field field, Object obj, Object value) {
        Method method = getSetterMethodByField(field.getName(), obj.getClass());
        ReflectionUtils.invokeMethod(method, obj, value);
    }

    /**
     * 获取setter方法
     *
     * @param field field
     * @param clazz class
     * @return setter method
     */
    public static Method getSetterMethodByField(Field field, Class<?> clazz) {
        String name = field.getName();
        return getSetterMethodByField(name, clazz);
    }

    public static Method getSetterMethodByField(String fieldName, Class<?> clazz) {
        return SETTER_CACHE.computeIfAbsent(new PrimKey(clazz, fieldName), primKey -> {
            String setterName = "set" + primKey.getFieldName().substring(0, 1).toUpperCase() + primKey.getFieldName().substring(1);
            return ReflectionUtils.findMethod(clazz, setterName);
        });
    }

    public static Method getGetterMethodByField(String fieldName, Class<?> clazz) {
        return GETTER_CACHE.computeIfAbsent(new PrimKey(clazz, fieldName), primKey -> {
            String getterName = "get" + primKey.getFieldName().substring(0, 1).toUpperCase()
                    + primKey.getFieldName().substring(1);
            return ReflectionUtils.findMethod(clazz, getterName);
        });

    }

    public static Method getGetterMethodByField(Field field, Class<?> clazz) {
        String name = field.getName();
        return getGetterMethodByField(name, clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object target, Field field) {
        try {
            return (T) CachedFieldUtils.getGetterMethodByField(field, target.getClass()).invoke(target);
        } catch (IllegalAccessException | InvocationTargetException ignore) {
        }
        ReflectionUtils.makeAccessible(field);
        try {
            return (T) field.get(target);
        } catch (IllegalAccessException ignore) {
        }
        throw new RuntimeException("invoke get " + field.getName() + " error," +
                "consider add a getter method for "
                + target.getClass().getSimpleName() + "." + field.getName());
    }
}
