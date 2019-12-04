package site.zido.coffee.security.configurers;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import site.zido.coffee.security.token.JwtSecurityContextRepository;
import site.zido.coffee.security.token.JwtTokenProvider;
import site.zido.coffee.security.token.TokenProvider;

import java.util.concurrent.TimeUnit;

/**
 * @author zido
 */
public class RestSecurityContextConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractHttpConfigurer<RestSecurityContextConfigurer<H>, H> {
    private TokenProvider tokenProvider;

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
                    this.tokenProvider = new JwtTokenProvider("coffee-jwt", 24 * 60 * 60 * 1000);
                }
            }
            JwtSecurityContextRepository repo = new JwtSecurityContextRepository(this.tokenProvider);
            AuthenticationTrustResolver trustResolver = restHttp
                    .getSharedObject(AuthenticationTrustResolver.class);
            if (trustResolver != null) {
                repo.setTrustResolver(trustResolver);
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

    public JwtTokenProviderConfig jwt() {
        return new JwtTokenProviderConfig(getBuilder());
    }

    public class JwtTokenProviderConfig {
        private JwtTokenProvider tokenProvider;
        private H restHttp;

        private JwtTokenProviderConfig(H restHttp) {
            this.restHttp = restHttp;
            enable();
        }

        public JwtTokenProviderConfig enable() {
            restHttp.setSharedObject(JwtTokenProvider.class, new JwtTokenProvider());
            return this;
        }

        public RestSecurityContextConfigurer<H> disable() {
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

        public RestSecurityContextConfigurer<H> and() {
            return RestSecurityContextConfigurer.this;
        }
    }
}
