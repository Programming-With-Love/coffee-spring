package site.zido.coffee.autoconfigure.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.servlet.DispatcherServlet;
import site.zido.coffee.mvc.rest.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 全局通用自动配置大杂烩
 *
 * @author zido
 */
@Configuration
@ConditionalOnBean({ObjectMapper.class, DispatcherServlet.class})
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class CommonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(HttpResponseBodyFactory.class)
    public HttpResponseBodyFactory bodyFactory() {
        return new DefaultHttpResponseBodyFactory();
    }

    /**
     * 全局异常处理
     *
     * @return exception advice
     */
    @Bean
    @ConditionalOnMissingBean(BaseGlobalExceptionHandler.class)
    public GlobalExceptionAdvice advice() {
        return new GlobalExceptionAdvice(bodyFactory());
    }


    /**
     * 全局请求日志
     *
     * @return filter
     */
    @Bean
    @ConditionalOnMissingBean(AbstractRequestLoggingFilter.class)
    public AbstractRequestLoggingFilter filter() {
        AbstractRequestLoggingFilter filter = new AbstractRequestLoggingFilter() {
            final Logger logger = LoggerFactory.getLogger("RequestLog");

            @Override
            protected boolean shouldLog(HttpServletRequest request) {
                return logger.isInfoEnabled();
            }

            @Override
            protected void beforeRequest(HttpServletRequest httpServletRequest, String s) {
                logger.info(s);
            }

            @Override
            protected void afterRequest(HttpServletRequest httpServletRequest, String s) {
                logger.info(s);
            }
        };
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        return filter;
    }

    @Bean
    public StringToResultHttpMessageConverter stringToResultHttpMessageConverter() {
        return new StringToResultHttpMessageConverter();
    }

    /**
     * json序列化配置
     *
     * @author zido
     */
    @Configuration
    @ConditionalOnProperty(prefix = "site.zido.json.auto-switch",
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
