package site.zido.coffee.security.configurers;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import site.zido.coffee.security.token.JwtRefreshFilter;
import site.zido.coffee.security.token.JwtSecurityContextRepository;

/**
 * 使用token提供用户信息存储
 * <p>
 * 目前支持：
 *
 * <ul>
 *     <li>
 *         jwt token:通过{@link #jwt()}启用，默认情况下为有效期为一小时
 *          <ul>
 *            <li>每10分钟尝试一次续期(默认)</li>
 *            <li>客户端续期</li>
 *          </ul>
 *     </li>
 * </ul>
 *
 * @author zido
 */
public class RestSecurityContextConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractHttpConfigurer<RestSecurityContextConfigurer<H>, H> {
    private String authHeaderName;

    public RestSecurityContextConfigurer() {
    }

    public RestSecurityContextConfigurer<H> securityContextRepository(
            SecurityContextRepository securityContextRepository) {
        getBuilder().setSharedObject(SecurityContextRepository.class,
                securityContextRepository);
        return this;
    }

    @Override
    public void configure(H restHttp) {
        SecurityContextRepository securityContextRepository = restHttp
                .getSharedObject(SecurityContextRepository.class);
        if (securityContextRepository == null) {
            JwtSecurityContextRepository repository = new JwtSecurityContextRepository("coffee-jwt", 1000 * 60 * 60, 1000 * 60 * 10);
            UserDetailsService userDetailsService = restHttp.getSharedObject(UserDetailsService.class);
            repository.setUserService(userDetailsService);
            AuthenticationTrustResolver trustResolver = restHttp
                    .getSharedObject(AuthenticationTrustResolver.class);
            if (trustResolver != null) {
                repository.setTrustResolver(trustResolver);
            }
            if (this.authHeaderName != null) {
                repository.setAuthHeaderName(authHeaderName);
            }
            postProcess(repository);
            restHttp.setSharedObject(SecurityContextRepository.class, repository);
            securityContextRepository = repository;
        }
        SecurityContextPersistenceFilter securityContextFilter = new SecurityContextPersistenceFilter(
                securityContextRepository);
        securityContextFilter.setForceEagerSessionCreation(false);
        securityContextFilter = postProcess(securityContextFilter);
        restHttp.addFilter(securityContextFilter);

        JwtRefreshFilter refreshFilter = restHttp.getSharedObject(JwtRefreshFilter.class);
        if (refreshFilter != null) {
            if (this.authHeaderName != null) {
                refreshFilter.setAuthHeaderName(authHeaderName);
            }
            restHttp.addFilterBefore(refreshFilter, SecurityContextPersistenceFilter.class);
        }
    }

    public RestSecurityContextConfigurer<H> authHeaderName(String authHeaderName) {
        this.authHeaderName = authHeaderName;
        return this;
    }

    public JwtSecurityConfigurer jwt() {
        return new JwtSecurityConfigurer();
    }

    public class JwtSecurityConfigurer {

        private JwtSecurityContextRepository repository;
        private JwtRefreshFilter refreshFilter;

        private JwtSecurityConfigurer() {
            enable();
        }

        public JwtSecurityConfigurer enable() {
            if (this.repository == null) {
                this.repository =
                        new JwtSecurityContextRepository("coffee-jwt", 60 * 60 * 1000, 60 * 10 * 1000);
                getBuilder().setSharedObject(JwtSecurityContextRepository.class, repository);
            }
            return this;
        }

        public RestSecurityContextConfigurer<H> disable() {
            getBuilder().setSharedObject(JwtSecurityContextRepository.class, null);
            return RestSecurityContextConfigurer.this;
        }

        public JwtSecurityConfigurer userDetailsService(UserDetailsService userDetailsService) {
            repository.setUserService(userDetailsService);
            return this;
        }

        public JwtSecurityConfigurer secret(String jwtSecret) {
            repository.setSecret(jwtSecret);
            return this;
        }

        /**
         * 启用自动为即将过期的token发放新token
         *
         * @param renewInMs  重新发放的请求时间，毫秒数
         * @param expiration token过期时间，过期时间不能比renewInMs短
         * @return this
         */
        public JwtSecurityConfigurer autoRefresh(long renewInMs, long expiration) {
            repository.setJwtExpirationInMs(expiration);
            repository.setJwtRenewInMs(renewInMs);
            return this;
        }

        /**
         * 启用手动刷新过期token，将提供手动刷新接口
         *
         * @return this
         */
        public JwtSecurityConfigurer refresh() {
            if (refreshFilter == null) {
                refreshFilter = getBuilder().getSharedObject(JwtRefreshFilter.class);
                if (refreshFilter == null) {
                    refreshFilter = new JwtRefreshFilter();
                    getBuilder().setSharedObject(JwtRefreshFilter.class, refreshFilter);
                }
            }
            return this;
        }

        public JwtSecurityConfigurer authoritiesMapper(GrantedAuthoritiesMapper mapper) {
            repository.setAuthoritiesMapper(mapper);
            return this;
        }

        public H and() {
            return getBuilder();
        }
    }
}
