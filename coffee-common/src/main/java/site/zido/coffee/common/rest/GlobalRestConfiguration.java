package site.zido.coffee.common.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * 全局rest配置类
 *
 * @author zido
 */
public class GlobalRestConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalRestConfiguration.class);

    /**
     * 全局响应体封装类
     *
     * @return result annotations
     */
    @Bean
    @ConditionalOnMissingBean(GlobalResultHandler.class)
    public GlobalResultHandler handler(HttpResponseBodyFactory factory) {
        LOGGER.debug("Set global result to wrap " + Result.class);
        return new GlobalResultHandler(factory);
    }

    /**
     * 配合全局响应封装的消息转换器
     *
     * @param converter message converter
     * @return configure
     */
    @Bean
    public WebMvcConfigurerAdapter adapterForMessageConverter(StringToResultHttpMessageConverter converter) {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
                LOGGER.debug("Add json string http message converter");
                converters.add(0, converter);
            }
        };
    }
}
