package site.zido.coffee.common.model;

import org.springframework.lang.NonNull;
import site.zido.coffee.common.utils.BeanUtils;

/**
 * 输出转换器接口，提供一个convertFrom方法，将pojo转换到其他对象
 * <p>
 * 一般用于在逻辑处理完后返回DTO对象
 *
 * @param <DTO>    the implementation class type
 * @param <DOMAIN> domain type
 * @author johnniang
 */
public interface OutputConverter<DTO extends OutputConverter<DTO, DOMAIN>, DOMAIN> {

    /**
     * Convert from domain.(shallow)
     *
     * @param domain domain data
     * @return converted dto data
     */
    @SuppressWarnings("unchecked")
    @NonNull
    default <T extends DTO> T convertFrom(@NonNull DOMAIN domain) {

        BeanUtils.updateProperties(domain, this);

        return (T) this;
    }
}
