package com.hnqc.common.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnqc.common.pojo.Result;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 为string类型返回值定制的消息处理器，配合补充{@link GlobalResultHandler}。
 *
 * 默认spring对string的处理经过GlobalResultHandler之后会返回text/plain类型的json对象,在这个消息转换器中，专门处理string类型，
 * 给前端正确的application/json响应头。
 * @author zido
 */
public class StringToResultHttpMessageConverter extends AbstractHttpMessageConverter<String> {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private ObjectMapper mapper;

    public StringToResultHttpMessageConverter() {
        this(DEFAULT_CHARSET);
    }

    public StringToResultHttpMessageConverter(Charset charset) {
        super(charset, MediaType.APPLICATION_JSON_UTF8, MediaType.ALL);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return String.class == clazz;
    }

    @Override
    protected String readInternal(Class<? extends String> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        Charset charset = getContentTypeCharset(inputMessage.getHeaders().getContentType());
        return StreamUtils.copyToString(inputMessage.getBody(), charset);
    }

    @Override
    protected void writeInternal(String s, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        Charset charset = getContentTypeCharset(outputMessage.getHeaders().getContentType());
        if (s.startsWith("o:")) {
            StreamUtils.copy(s.substring(2), charset, outputMessage.getBody());
            return;
        }
        StreamUtils.copy(mapper.writeValueAsString(Result.success(s)), charset, outputMessage.getBody());
    }

    private Charset getContentTypeCharset(MediaType contentType) {
        if (contentType != null && contentType.getCharset() != null) {
            return contentType.getCharset();
        } else {
            return getDefaultCharset();
        }
    }

    public StringToResultHttpMessageConverter setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
        return this;
    }
}
