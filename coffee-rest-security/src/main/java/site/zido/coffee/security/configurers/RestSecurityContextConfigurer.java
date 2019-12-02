package site.zido.coffee.security.configurers;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import site.zido.coffee.security.token.JwtSecurityContextRepository;
import site.zido.coffee.security.token.JwtTokenProvider;
import site.zido.coffee.security.token.TokenProvider;

/**
 * @author zido
 */
public class RestSecurityContextConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractHttpConfigurer<RestSecurityContextConfigurer<H>, H> {
    private TokenProvider providedTokenProvider;

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
    public void configure(H restHttp) {
        SecurityContextRepository securityContextRepository = restHttp
                .getSharedObject(SecurityContextRepository.class);
        if (securityContextRepository == null) {
            TokenProvider provider = restHttp.getSharedObject(TokenProvider.class);
            if (provider == null) {
                if (this.providedTokenProvider == null) {
                    provider = new JwtTokenProvider("coffee-jwt", 24 * 60 * 60 * 1000);
                } else {
                    provider = this.providedTokenProvider;
                }
            }
            securityContextRepository = new JwtSecurityContextRepository(provider);
        }
        SecurityContextPersistenceFilter securityContextFilter = new SecurityContextPersistenceFilter(
                securityContextRepository);
        securityContextFilter.setForceEagerSessionCreation(false);
        securityContextFilter = postProcess(securityContextFilter);
        restHttp.addFilter(securityContextFilter);
    }

    public void setProvidedTokenProvider(JwtTokenProvider providedTokenProvider) {
        this.providedTokenProvider = providedTokenProvider;
    }
}
