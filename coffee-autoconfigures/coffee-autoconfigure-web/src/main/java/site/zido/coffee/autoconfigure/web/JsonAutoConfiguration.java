package site.zido.coffee.autoconfigure.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * json配置
 *
 * @author zido
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean({ObjectMapper.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "spring.coffee.json.auto-switch",
        value = "enable",
        havingValue = "true",
        matchIfMissing = true)
@AutoConfigureAfter(JacksonAutoConfiguration.class)
public class JsonAutoConfiguration {

    /**
     * 配置全局json序列化(开发模式)
     * <ul>
     * <li>null参与序列化</li>
     * </ul>
     */
    @Autowired
    public void setMapper(@Value("${spring.profiles.active:prod}") List<String> profiles,
                          ObjectMapper mapper) {
        if (profiles.contains("prod")) {
            prodObjectMapper(mapper);
        }
    }

    /**
     * 配置全局json序列化
     * <ul>
     * <li>null不参与序列化</li>
     * <li>属性不匹配会失败</li>
     * </ul>
     */
    private void prodObjectMapper(ObjectMapper mapper) {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
