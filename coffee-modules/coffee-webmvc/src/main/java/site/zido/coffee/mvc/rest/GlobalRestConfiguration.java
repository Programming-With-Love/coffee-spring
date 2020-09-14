package site.zido.coffee.mvc.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import site.zido.coffee.mvc.exceptions.EnableGlobalException;

import java.util.List;

/**
 * 全局rest配置类
 *
 * <ul>
 *     <li>开启全局异常处理</li>
 *     <li>开启全局rest响应体封装</li>
 *     <li>rest</li>
 * </ul>
 *
 * @author zido
 */
@Configuration
@Import({HttpResponseBodyConfiguration.class})
@EnableGlobalException
public class GlobalRestConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalRestConfiguration.class);

    /**
     * 全局响应体封装类
     *
     * @return result annotations
     */
    @Bean
    public GlobalResultHandler handler(HttpResponseBodyFactory factory) {
        LOGGER.debug("The global result is enabled");
        return new GlobalResultHandler(factory);
    }

    /**
     * 配合全局响应封装的消息转换器
     */
    @Configuration
    @EnableWebMvc
    public static class StringToResultHttpConfiguration implements WebMvcConfigurer {
        private final ObjectMapper mapper;

        public StringToResultHttpConfiguration(ObjectMapper mapper) {
            this.mapper = mapper;
        }

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            LOGGER.debug("Add json string http message converter");
            converters.add(0, new StringToResultHttpMessageConverter(mapper));
        }
    }
}
