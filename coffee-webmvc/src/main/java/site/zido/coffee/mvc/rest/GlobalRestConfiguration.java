package site.zido.coffee.mvc.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import site.zido.coffee.mvc.common.HttpResponseBodyConfiguration;

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
@Import(HttpResponseBodyConfiguration.class)
public class GlobalRestConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalRestConfiguration.class);

    @Bean
    public GlobalExceptionAdvice advice(HttpResponseBodyFactory factory) {
        return new GlobalExceptionAdvice(factory);
    }

    /**
     * 全局响应体封装类
     *
     * @return result annotations
     */
    @Bean
    public GlobalResultHandler handler(HttpResponseBodyFactory factory) {
        LOGGER.debug("Set global result to wrap " + Result.class);
        return new GlobalResultHandler(factory);
    }

    @Bean
    public StringToResultHttpMessageConverter stringToResultHttpMessageConverter() {
        return new StringToResultHttpMessageConverter();
    }

    /**
     * 配合全局响应封装的消息转换器
     *
     * @param converter message converter
     * @return configure
     */
    @Bean
    public WebMvcConfigurer adapterForMessageConverter(StringToResultHttpMessageConverter converter) {
        return new WebMvcConfigurer() {
            @Override
            public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
                LOGGER.debug("Add json string http message converter");
                converters.add(0, converter);
            }
        };
    }
}
