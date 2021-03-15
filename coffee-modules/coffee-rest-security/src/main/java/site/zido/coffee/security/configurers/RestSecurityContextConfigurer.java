package site.zido.coffee.security.configurers;

import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import site.zido.coffee.core.utils.RandomUtils;
import site.zido.coffee.core.utils.SpringUtils;
import site.zido.coffee.security.token.JwtRefreshFilter;
import site.zido.coffee.security.token.JwtSecurityContextRepository;

/**
 * 使用token提供用户信息存储
 * <p>
 * 目前支持：
 *
 * <ul>
 * <li>jwt token:通过{@link #jwt()}启用，默认情况下为有效期为一小时
 * <ul>
 * <li>每10分钟尝试一次续期(默认)</li>
 * <li>客户端续期</li>
 * </ul>
 * </li>
 * </ul>
 *
 * @author zido
 */
public class RestSecurityContextConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractHttpConfigurer<RestSecurityContextConfigurer<H>, H> {

    private JwtSecurityConfigurer configurer;

    public RestSecurityContextConfigurer() {
    }

    public RestSecurityContextConfigurer<H> securityContextRepository(
            JwtSecurityContextRepository securityContextRepository) {
        getBuilder().setSharedObject(JwtSecurityContextRepository.class, securityContextRepository);
        return this;
    }

    @Override
    public void init(H builder) throws Exception {
        super.init(builder);
    }

    @Override
    public void configure(H http) {
        if (configurer != null) {
            configurer.apply(http);
        }
        SecurityContextRepository securityContextRepository = http.getSharedObject(SecurityContextRepository.class);
        if (securityContextRepository == null) {
            JwtSecurityContextRepository repository = new JwtSecurityContextRepository("coffee-jwt", 1000 * 60 * 60,
                    1000 * 60 * 10);
            UserDetailsService userDetailsService = http.getSharedObject(UserDetailsService.class);
            repository.setUserService(userDetailsService);
            AuthenticationTrustResolver trustResolver = http.getSharedObject(AuthenticationTrustResolver.class);
            if (trustResolver != null) {
                repository.setTrustResolver(trustResolver);
            }
            repository.setAuthHeaderName("Authorization");
            postProcess(repository);
            http.setSharedObject(SecurityContextRepository.class, repository);
            securityContextRepository = repository;
        }
        SecurityContextPersistenceFilter securityContextFilter = new SecurityContextPersistenceFilter(
                securityContextRepository);
        securityContextFilter.setForceEagerSessionCreation(false);
        securityContextFilter = postProcess(securityContextFilter);
        http.addFilter(securityContextFilter);

        JwtRefreshFilter refreshFilter = http.getSharedObject(JwtRefreshFilter.class);
        if (refreshFilter != null) {
            http.addFilterBefore(refreshFilter, SecurityContextPersistenceFilter.class);
        }
    }

    public JwtSecurityConfigurer jwt() {
        return (configurer = new JwtSecurityConfigurer());
    }

    public class JwtSecurityConfigurer {
        private boolean enable = false;
        private boolean refresh = true;
        private String refreshHeader = "Refresh-Token";
        private String refreshSecret;
        private boolean autoRefresh = false;
        private String secret;
        private String header = "Authorization";
        private long renewInMs = 10 * 60 * 1000;
        private long expiration = 3600 * 1000;
        private String keyPrefix = "coffee:jwt:";
        private long timeout;
        private String issue;
        private String refreshUrl;
        private long refreshExpiration = 3600 * 1000 * 24;

        private GrantedAuthoritiesMapper authoritiesMapper;
        private UserDetailsService userService;
        private AuthenticationTrustResolver trustResolver;

        public JwtSecurityConfigurer() {
            this.enable(true);
            this.autoRefresh(true);
        }

        private void apply(H http) {
            if (!enable) {
                return;
            }
            if (this.secret == null) {
                this.secret = RandomUtils.ascii(12);
            }
            if (autoRefresh) {
                JwtSecurityContextRepository repository = new JwtSecurityContextRepository(secret, expiration,
                        renewInMs);
                if (header != null) {
                    repository.setAuthHeaderName(header);
                }
                if (authoritiesMapper != null) {
                    repository.setAuthoritiesMapper(authoritiesMapper);
                }
                if (issue != null) {
                    repository.setIssue(issue);
                }
                if (trustResolver != null) {
                    repository.setTrustResolver(trustResolver);
                }
                if (userService != null
                        || (userService = SpringUtils.getBeanOrNull(http.getSharedObject(ApplicationContext.class),
                        UserDetailsService.class)) != null
                        || (userService = getBuilder().getSharedObject(UserDetailsService.class)) != null) {
                    repository.setUserService(userService);
                }
                getBuilder().setSharedObject(SecurityContextRepository.class, postProcess(repository));
            }

            if (refresh) {
                JwtRefreshFilter refreshFilter = getBuilder().getSharedObject(JwtRefreshFilter.class);
                if (refreshFilter == null) {
                    refreshFilter = new JwtRefreshFilter();
                    getBuilder().setSharedObject(JwtRefreshFilter.class, refreshFilter);
                }
                if (refreshUrl != null) {
                    refreshFilter.setProcessUrl(refreshUrl);
                }
                if (refreshHeader != null) {
                    refreshFilter.setRefreshHeader(refreshHeader);
                }
                if (userService != null) {
                    refreshFilter.setUserService(userService);
                }
                if (authoritiesMapper != null) {
                    refreshFilter.setAuthoritiesMapper(authoritiesMapper);
                }
                if (issue != null) {
                    refreshFilter.setIssue(issue);
                }
                refreshFilter.setJwtExpirationInMs(expiration);
                refreshFilter.setJwtSecret(secret);
                if (refreshSecret != null) {
                    refreshFilter.setRefreshSecret(refreshSecret);
                } else if (secret != null) {
                    refreshFilter.setRefreshSecret(secret);
                } else {
                    throw new IllegalStateException("请至少设置jwt加解密密钥secret");
                }
                refreshFilter.setRefreshTokenExpirationInMs(refreshExpiration);
                getBuilder().setSharedObject(JwtRefreshFilter.class, postProcess(refreshFilter));
            }

        }

        public boolean isEnable() {
            return enable;
        }

        public JwtSecurityConfigurer enable(boolean enable) {
            this.enable = enable;
            return this;
        }

        public boolean isRefresh() {
            return refresh;
        }

        public JwtSecurityConfigurer refresh(boolean refresh) {
            this.refresh = refresh;
            return this;
        }

        public String getRefreshHeader() {
            return refreshHeader;
        }

        public JwtSecurityConfigurer refreshHeader(String refreshHeader) {
            this.refreshHeader = refreshHeader;
            return this;
        }

        public String getRefreshSecret() {
            return refreshSecret;
        }

        public JwtSecurityConfigurer refreshSecret(String refreshSecret) {
            this.refreshSecret = refreshSecret;
            return this;
        }

        public boolean isAutoRefresh() {
            return autoRefresh;
        }

        public JwtSecurityConfigurer autoRefresh(boolean autoRefresh) {
            this.autoRefresh = autoRefresh;
            return this;
        }

        public String getSecret() {
            return secret;
        }

        public JwtSecurityConfigurer secret(String secret) {
            this.secret = secret;
            return this;
        }

        public String getHeader() {
            return header;
        }

        public JwtSecurityConfigurer header(String header) {
            this.header = header;
            return this;
        }

        public long getRenewInMs() {
            return renewInMs;
        }

        public JwtSecurityConfigurer renewInMs(long renewInMs) {
            this.renewInMs = renewInMs;
            return this;
        }

        public long getExpiration() {
            return expiration;
        }

        public JwtSecurityConfigurer expiration(long expiration) {
            this.expiration = expiration;
            return this;
        }

        public String getKeyPrefix() {
            return keyPrefix;
        }

        public JwtSecurityConfigurer keyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
            return this;
        }

        public long getTimeout() {
            return timeout;
        }

        public JwtSecurityConfigurer timeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        public String getIssue() {
            return issue;
        }

        public JwtSecurityConfigurer issue(String issue) {
            this.issue = issue;
            return this;
        }

        public String getRefreshUrl() {
            return refreshUrl;
        }

        public JwtSecurityConfigurer refreshUrl(String refreshUrl) {
            this.refreshUrl = refreshUrl;
            return this;
        }

        public long getRefreshExpiration() {
            return refreshExpiration;
        }

        public JwtSecurityConfigurer refreshExpiration(long refreshExpiration) {
            this.refreshExpiration = refreshExpiration;
            return this;
        }

        public GrantedAuthoritiesMapper getAuthoritiesMapper() {
            return authoritiesMapper;
        }

        public JwtSecurityConfigurer authoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper) {
            this.authoritiesMapper = authoritiesMapper;
            return this;
        }

        public UserDetailsService getUserService() {
            return userService;
        }

        public JwtSecurityConfigurer userService(UserDetailsService userService) {
            this.userService = userService;
            return this;
        }

        public AuthenticationTrustResolver getTrustResolver() {
            return trustResolver;
        }

        public JwtSecurityConfigurer trustResolver(AuthenticationTrustResolver trustResolver) {
            this.trustResolver = trustResolver;
            return this;
        }

        public H and() {
            return getBuilder();
        }
    }
}
