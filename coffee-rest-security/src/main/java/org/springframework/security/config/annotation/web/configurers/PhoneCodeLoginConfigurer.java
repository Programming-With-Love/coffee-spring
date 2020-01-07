package org.springframework.security.config.annotation.web.configurers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import site.zido.coffee.security.authentication.phone.*;

/**
 * @author zido
 */
public class PhoneCodeLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractRestAuthenticationFilterConfigurer<H, PhoneCodeLoginConfigurer<H>, PhoneAuthenticationFilter> {
    private static Logger LOGGER = LoggerFactory.getLogger(PhoneCodeLoginConfigurer.class);
    private PhoneCodeService phoneCodeService;
    private PhoneCodeCache cache;
    private CodeGenerator codeGenerator;
    private CodeValidator codeValidator;

    public PhoneCodeLoginConfigurer() {
        super(new PhoneAuthenticationFilter(), null);
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }

    @Override
    public void init(H http) throws Exception {
        super.init(http);
        PermitAllSupport.permitAll(http, getAuthenticationFilter().getCodeRequestMatcher());
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);
        if (phoneCodeService == null) {
            phoneCodeService = http.getSharedObject(PhoneCodeService.class);
            if (phoneCodeService == null) {
                try {
                    phoneCodeService = context.getBean(PhoneCodeService.class);
                } catch (BeansException e) {
                    phoneCodeService = defaultPhoneCodeService();
                }
                http.setSharedObject(PhoneCodeService.class, phoneCodeService);
            }
        }
        if (codeGenerator == null) {
            codeGenerator = http.getSharedObject(CodeGenerator.class);
            if (codeGenerator == null) {
                try {
                    codeGenerator = context.getBean(CodeGenerator.class);
                    http.setSharedObject(CodeGenerator.class, codeGenerator);
                } catch (BeansException ignore) {
                }
            } else {
                http.setSharedObject(CodeGenerator.class, codeGenerator);
            }
        }
        if (cache == null) {
            cache = http.getSharedObject(PhoneCodeCache.class);
            try {
                cache = context.getBean(PhoneCodeCache.class);
            } catch (BeansException ignore) {
                SpringRedisPhoneCodeCache cache = new SpringRedisPhoneCodeCache();
                StringRedisTemplate template = context.getBean(StringRedisTemplate.class);
                cache.setTemplate(template);
                this.cache = cache;
            }
        }
        http.setSharedObject(PhoneCodeCache.class, cache);
    }

    private PhoneCodeService defaultPhoneCodeService() {
        LOGGER.warn("使用控制台输出手机号验证码仅用于调试，" +
                "如果需要真实发送验证码请实现 PhoneCodeService" +
                "注入到spring容器中");
        return new PhoneCodeService() {
            private Logger logger = LoggerFactory.getLogger(PhoneCodeService.class);

            @Override
            public void sendCode(String mobile, String code) {
                logger.info("mobile:{},code:{}", mobile, code);
            }
        };
    }

    @Override
    public void configure(H http) throws Exception {
        PhoneCodeService phoneCodeService = http.getSharedObject(PhoneCodeService.class);
        getAuthenticationFilter().setPhoneCodeService(phoneCodeService);
        PhoneCodeCache cache = http.getSharedObject(PhoneCodeCache.class);
        getAuthenticationFilter().setCache(cache);
        PhoneAuthUserAuthenticationProvider provider = new PhoneAuthUserAuthenticationProvider();
        provider.setPhoneCodeCache(cache);
        if (codeValidator != null) {
            provider.setCodeValidator(codeValidator);
        }
        GrantedAuthoritiesMapper mapper = http.getSharedObject(GrantedAuthoritiesMapper.class);
        if (mapper != null) {
            provider.setAuthoritiesMapper(mapper);
        }
        UserDetailsService userDetailsService = http.getSharedObject(UserDetailsService.class);
        provider.setUserDetailsService(userDetailsService);
        postProcess(provider);
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.authenticationProvider(provider);
        CodeGenerator codeGenerator = http.getSharedObject(CodeGenerator.class);
        if (codeGenerator != null) {
            getAuthenticationFilter().setCodeGenerator(codeGenerator);
        }
        super.configure(http);
    }

    public PhoneCodeLoginConfigurer<H> codeValidator(CodeValidator validator) {
        codeValidator = validator;
        return this;
    }

    public PhoneCodeLoginConfigurer<H> setPhoneParameter(String parameter) {
        getAuthenticationFilter().setPhoneParameter(parameter);
        return this;
    }

    public PhoneCodeLoginConfigurer<H> setCodeParameter(String code) {
        getAuthenticationFilter().setCodeParameter(code);
        return this;
    }

    public PhoneCodeLoginConfigurer<H> codeProcessingUrl(String url) {
        getAuthenticationFilter().setCodeRequestMatcher(createLoginProcessingUrlMatcher(url));
        return this;
    }

    public PhoneCodeLoginConfigurer<H> codeCachePrefix(String prefix) {
        getAuthenticationFilter().setCachePrefix(prefix);
        return this;
    }

    public PhoneCodeLoginConfigurer<H> phoneCodeService(PhoneCodeService phoneCodeService) {
        this.phoneCodeService = phoneCodeService;
        return this;
    }

    public PhoneCodeLoginConfigurer<H> codeGenerator(CodeGenerator codeGenerator) {
        this.codeGenerator = codeGenerator;
        return this;
    }

    public CustomCodeGeneratorConfig customCode() {
        return new CustomCodeGeneratorConfig();
    }

    public PhoneCodeLoginConfigurer<H> phoneCodeCache(PhoneCodeCache phoneCodeCache) {
        this.cache = phoneCodeCache;
        return this;
    }

    private class CustomCodeGeneratorConfig {
        private CustomCodeGenerator customCodeGenerator;

        private CustomCodeGeneratorConfig() {
            enable();
        }

        private CustomCodeGeneratorConfig enable() {
            this.customCodeGenerator = new CustomCodeGenerator();
            getBuilder().setSharedObject(CodeGenerator.class, customCodeGenerator);
            return this;
        }

        public PhoneCodeLoginConfigurer<H> disable() {
            getBuilder().setSharedObject(CodeGenerator.class, null);
            this.customCodeGenerator = null;
            return PhoneCodeLoginConfigurer.this;
        }

        public CustomCodeGeneratorConfig modes(CustomCodeGenerator.Mode... modes) {
            customCodeGenerator.setMode(modes);
            return this;
        }

        public CustomCodeGeneratorConfig len(int len) {
            customCodeGenerator.setMaxLength(len);
            customCodeGenerator.setMinLength(len);
            return this;
        }

        public CustomCodeGeneratorConfig maxLen(int len) {
            customCodeGenerator.setMaxLength(len);
            return this;
        }

        public CustomCodeGeneratorConfig minLen(int len) {
            customCodeGenerator.setMinLength(len);
            return this;
        }

        public CustomCodeGeneratorConfig addChar(char[] chars) {
            customCodeGenerator.addArr(chars);
            return this;
        }

        public H and() {
            return getBuilder();
        }
    }
}
