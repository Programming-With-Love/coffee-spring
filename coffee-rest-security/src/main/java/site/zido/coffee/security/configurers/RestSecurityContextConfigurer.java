package site.zido.coffee.security.configurers;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import site.zido.coffee.security.token.RestSecurityContextRepository;
import site.zido.coffee.security.token.JwtTokenProvider;
import site.zido.coffee.security.token.TokenProvider;

import java.util.concurrent.TimeUnit;

/**
 * @author zido
 */
public class RestSecurityContextConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractHttpConfigurer<RestSecurityContextConfigurer<H>, H> {
    private TokenProvider tokenProvider;
    private String authHeaderName;

    /**
     * Creates a new instance
     *
     * @see HttpSecurity#securityContext()
     */
    public RestSecurityContextConfigurer() {
    }

    /**
     * Specifies the shared {@link SecurityContextRepository} that is to be used
     *
     * @param securityContextRepository the {@link SecurityContextRepository} to use
     * @return the {@link HttpSecurity} for further customizations
     */
    public RestSecurityContextConfigurer<H> securityContextRepository(
            SecurityContextRepository securityContextRepository) {
        getBuilder().setSharedObject(SecurityContextRepository.class,
                securityContextRepository);
        return this;
    }

    @Override
    public void init(H restHttp) throws Exception {
        SecurityContextRepository securityContextRepository = restHttp
                .getSharedObject(SecurityContextRepository.class);
        if (securityContextRepository == null) {
            if (this.tokenProvider == null) {
                this.tokenProvider = restHttp.getSharedObject(TokenProvider.class);
                if (this.tokenProvider == null) {
                    JwtTokenProvider provider = new JwtTokenProvider("coffee-jwt", 24 * 60 * 60 * 1000);
                    UserDetailsService userDetailsService = restHttp.getSharedObject(UserDetailsService.class);
                    provider.setUserService(userDetailsService);
                    postProcess(provider);
                    this.tokenProvider = provider;
                }
            }
            RestSecurityContextRepository repo = new RestSecurityContextRepository(this.tokenProvider);
            AuthenticationTrustResolver trustResolver = restHttp
                    .getSharedObject(AuthenticationTrustResolver.class);
            if (trustResolver != null) {
                repo.setTrustResolver(trustResolver);
            }
            if (this.authHeaderName != null) {
                repo.setAuthHeaderName(authHeaderName);
            }
            restHttp.setSharedObject(SecurityContextRepository.class, repo);
        }
    }

    @Override
    public void configure(H restHttp) {
        SecurityContextRepository securityContextRepository = restHttp
                .getSharedObject(SecurityContextRepository.class);
        SecurityContextPersistenceFilter securityContextFilter = new SecurityContextPersistenceFilter(
                securityContextRepository);
        securityContextFilter.setForceEagerSessionCreation(false);
        securityContextFilter = postProcess(securityContextFilter);
        restHttp.addFilter(securityContextFilter);
    }

    public RestSecurityContextConfigurer<H> tokenProvider(TokenProvider providedTokenProvider) {
        this.tokenProvider = providedTokenProvider;
        return this;
    }

    public RestSecurityContextConfigurer<H> authHeaderName(String authHeaderName) {
        this.authHeaderName = authHeaderName;
        return this;
    }

    public JwtTokenProviderConfig jwt() {
        return new JwtTokenProviderConfig();
    }

    public class JwtTokenProviderConfig {
        private JwtTokenProvider tokenProvider;

        private JwtTokenProviderConfig() {
            enable();
        }

        public JwtTokenProviderConfig enable() {
            if (this.tokenProvider == null) {
                this.tokenProvider = new JwtTokenProvider();
                getBuilder().setSharedObject(JwtTokenProvider.class, tokenProvider);
            }
            return this;
        }

        public RestSecurityContextConfigurer<H> disable() {
            getBuilder().setSharedObject(JwtTokenProvider.class, null);
            this.tokenProvider = null;
            return RestSecurityContextConfigurer.this;
        }

        public JwtTokenProviderConfig userDetailsService(UserDetailsService userDetailsService) {
            tokenProvider.setUserService(userDetailsService);
            return this;
        }

        public JwtTokenProviderConfig jwtSecret(String jwtSecret) {
            tokenProvider.setJwtSecret(jwtSecret);
            return this;
        }

        public JwtTokenProviderConfig jwtExpirationInMs(int jwtExpirationInMs) {
            tokenProvider.setJwtExpirationInMs(jwtExpirationInMs);
            return this;
        }

        public JwtTokenProviderConfig jwtExpiration(long time, TimeUnit unit) {
            tokenProvider.setJwtExpirationInMs(unit.toMillis(time));
            return this;
        }

        public JwtTokenProviderConfig authoritiesMapper(GrantedAuthoritiesMapper mapper) {
            tokenProvider.setAuthoritiesMapper(mapper);
            return this;
        }

        public H and() {
            return getBuilder();
        }
    }
}
