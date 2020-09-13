package site.zido.coffee.autoconfigure.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.List;

/**
 * 全局通用自动配置大杂烩
 *
 * @author zido
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean({ObjectMapper.class, DispatcherServlet.class})
public class CommonAutoConfiguration {

    /**
     * json序列化配置
     *
     * @author zido
     */
    @Configuration
    @ConditionalOnProperty(prefix = "spring.coffee.json.auto-switch",
            value = "enable",
            havingValue = "true",
            matchIfMissing = true)
    @AutoConfigureAfter(JacksonAutoConfiguration.class)
    @ConditionalOnBean(ObjectMapper.class)
    public static class JsonAutoConfiguration {
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
}
