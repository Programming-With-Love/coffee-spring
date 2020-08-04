package site.zido.coffee.core.common.model;

import org.springframework.lang.Nullable;
import site.zido.coffee.common.utils.BeanUtils;
import site.zido.coffee.common.utils.ReflectionUtils;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;

/**
 * 输入转换器接口，提供一个convertTo方法，转换到目标pojo
 * <p>
 * 一般用于controller中传递对象转换到实际处理的entity或者pojo
 *
 * @param <DOMAIN>
 */
public interface InputConverter<DOMAIN> {

    /**
     * Convert to domain.(shallow)
     *
     * @return new domain with same value(not null)
     */
    @SuppressWarnings("unchecked")
    default DOMAIN convertTo() {
        ParameterizedType currentType = parameterizedType();

        Objects.requireNonNull(currentType, "Cannot fetch actual type because parameterized type is null");

        Class<DOMAIN> domainClass = (Class<DOMAIN>) currentType.getActualTypeArguments()[0];

        return BeanUtils.transformFrom(this, domainClass);
    }

    default void update(DOMAIN domain) {
        BeanUtils.updateProperties(this, domain);
    }

    /**
     * Get parameterized type.
     *
     * @return parameterized type or null
     */
    @Nullable
    default ParameterizedType parameterizedType() {
        return ReflectionUtils.getParameterizedType(InputConverter.class, this.getClass());
    }
}
