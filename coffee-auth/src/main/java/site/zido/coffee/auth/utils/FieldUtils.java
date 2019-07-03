package site.zido.coffee.auth.utils;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 属性注入工具
 *
 * @author zido
 */
public class FieldUtils {
    private static final Map<PrimKey, Method> setterCache = new ConcurrentHashMap<>(6);

    private static final class PrimKey {
        private Class<?> clazz;
        private Field field;

        public PrimKey(Class<?> clazz, Field field) {
            this.clazz = clazz;
            this.field = field;
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
                    Objects.equals(field, primKey.field);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clazz, field);
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public void setClazz(Class<?> clazz) {
            this.clazz = clazz;
        }

        public Field getField() {
            return field;
        }

        public void setField(Field field) {
            this.field = field;
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
        Method method = setterCache.computeIfAbsent(new PrimKey(obj.getClass(), field),
                primKey -> getSetterMethodByField(primKey.getField(), primKey.getClazz()));
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
        String setterName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
        return ReflectionUtils.findMethod(clazz, setterName, field.getType());
    }

    public static Method getGetterMethodByField(Field field, Class<?> clazz) {
        String name = field.getName();
        String setterName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
        return ReflectionUtils.findMethod(clazz, setterName, field.getType());
    }
}
