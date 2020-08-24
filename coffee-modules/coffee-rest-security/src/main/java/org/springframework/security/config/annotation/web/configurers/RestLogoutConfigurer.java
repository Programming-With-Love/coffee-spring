package org.springframework.security.config.annotation.web.configurers;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.authentication.logout.*;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import site.zido.coffee.security.configurers.RestLogoutSuccessHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 登出配置
 *
 * @author zido
 */
public class RestLogoutConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractHttpConfigurer<LogoutConfigurer<H>, H> {
    private List<LogoutHandler> logoutHandlers = new ArrayList<>();
    private SecurityContextLogoutHandler contextLogoutHandler = new SecurityContextLogoutHandler();
    private String logoutSuccessUrl = "/login?logout";
    private LogoutSuccessHandler logoutSuccessHandler;
    private String logoutUrl = "/logout";
    private RequestMatcher logoutRequestMatcher;
    private boolean permitAll;
    private boolean customLogoutSuccess;

    private LinkedHashMap<RequestMatcher, LogoutSuccessHandler> defaultLogoutSuccessHandlerMappings =
            new LinkedHashMap<>();

    public RestLogoutConfigurer() {
    }

    public RestLogoutConfigurer<H> addLogoutHandler(LogoutHandler logoutHandler) {
        Assert.notNull(logoutHandler, "logoutHandler cannot be null");
        this.logoutHandlers.add(logoutHandler);
        return this;
    }

    public RestLogoutConfigurer<H> clearAuthentication(boolean clearAuthentication) {
        contextLogoutHandler.setClearAuthentication(clearAuthentication);
        return this;
    }


    public RestLogoutConfigurer<H> logoutUrl(String logoutUrl) {
        this.logoutRequestMatcher = null;
        this.logoutUrl = logoutUrl;
        return this;
    }

    public RestLogoutConfigurer<H> logoutRequestMatcher(RequestMatcher logoutRequestMatcher) {
        this.logoutRequestMatcher = logoutRequestMatcher;
        return this;
    }

    public RestLogoutConfigurer<H> permitAll() {
        return permitAll(true);
    }

    public RestLogoutConfigurer<H> logoutSuccessHandler(
            LogoutSuccessHandler logoutSuccessHandler) {
        this.logoutSuccessUrl = null;
        this.customLogoutSuccess = true;
        this.logoutSuccessHandler = logoutSuccessHandler;
        return this;
    }

    public RestLogoutConfigurer<H> defaultLogoutSuccessHandlerFor(
            LogoutSuccessHandler handler, RequestMatcher preferredMatcher) {
        Assert.notNull(handler, "handler cannot be null");
        Assert.notNull(preferredMatcher, "preferredMatcher cannot be null");
        this.defaultLogoutSuccessHandlerMappings.put(preferredMatcher, handler);
        return this;
    }

    public RestLogoutConfigurer<H> permitAll(boolean permitAll) {
        this.permitAll = permitAll;
        return this;
    }

    private LogoutSuccessHandler getLogoutSuccessHandler() {
        LogoutSuccessHandler handler = this.logoutSuccessHandler;
        if (handler == null) {
            handler = createDefaultSuccessHandler();
        }
        return handler;
    }

    private LogoutSuccessHandler createDefaultSuccessHandler() {
        RestLogoutSuccessHandler logoutHandler = new RestLogoutSuccessHandler();
        if (defaultLogoutSuccessHandlerMappings.isEmpty()) {
            return logoutHandler;
        }
        DelegatingLogoutSuccessHandler successHandler = new DelegatingLogoutSuccessHandler(defaultLogoutSuccessHandlerMappings);
        successHandler.setDefaultLogoutSuccessHandler(logoutHandler);
        return successHandler;
    }

    @Override
    public void init(H http) {
        if (permitAll) {
            PermitAllSupport.permitAll(http, this.logoutSuccessUrl);
            PermitAllSupport.permitAll(http, this.getLogoutRequestMatcher(http));
        }
    }

    @Override
    public void configure(H http) throws Exception {
        LogoutFilter logoutFilter = createLogoutFilter(http);
        http.addFilter(logoutFilter);
    }

    boolean isCustomLogoutSuccess() {
        return customLogoutSuccess;
    }

    private String getLogoutSuccessUrl() {
        return logoutSuccessUrl;
    }

    List<LogoutHandler> getLogoutHandlers() {
        return logoutHandlers;
    }

    private LogoutFilter createLogoutFilter(H http) {
        logoutHandlers.add(contextLogoutHandler);
        logoutHandlers.add(postProcess(new LogoutSuccessEventPublishingLogoutHandler()));
        LogoutHandler[] handlers = logoutHandlers
                .toArray(new LogoutHandler[0]);
        LogoutFilter result = new LogoutFilter(getLogoutSuccessHandler(), handlers);
        result.setLogoutRequestMatcher(getLogoutRequestMatcher(http));
        result = postProcess(result);
        return result;
    }

    @SuppressWarnings("unchecked")
    private RequestMatcher getLogoutRequestMatcher(H http) {
        if (logoutRequestMatcher != null) {
            return logoutRequestMatcher;
        }
        if (http.getConfigurer(CsrfConfigurer.class) != null) {
            this.logoutRequestMatcher = new AntPathRequestMatcher(this.logoutUrl, "POST");
        } else {
            this.logoutRequestMatcher = new OrRequestMatcher(
                    new AntPathRequestMatcher(this.logoutUrl, "GET"),
                    new AntPathRequestMatcher(this.logoutUrl, "POST"),
                    new AntPathRequestMatcher(this.logoutUrl, "PUT"),
                    new AntPathRequestMatcher(this.logoutUrl, "DELETE")
            );
        }
        return this.logoutRequestMatcher;
    }
}
