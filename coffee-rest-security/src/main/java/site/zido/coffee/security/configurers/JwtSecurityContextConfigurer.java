package site.zido.coffee.security.configurers;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import site.zido.coffee.security.jwt.JwtSecurityContextRepository;
import site.zido.coffee.security.jwt.JwtTokenProvider;

/**
 * @author zido
 */
public class JwtSecurityContextConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractHttpConfigurer<JwtSecurityContextConfigurer<H>, H> {
    private JwtTokenProvider providedJwtTokenProvider;

    /**
     * Creates a new instance
     *
     * @see HttpSecurity#securityContext()
     */
    public JwtSecurityContextConfigurer() {
    }

    /**
     * Specifies the shared {@link SecurityContextRepository} that is to be used
     *
     * @param securityContextRepository the {@link SecurityContextRepository} to use
     * @return the {@link HttpSecurity} for further customizations
     */
    public JwtSecurityContextConfigurer<H> securityContextRepository(
            SecurityContextRepository securityContextRepository) {
        getBuilder().setSharedObject(SecurityContextRepository.class,
                securityContextRepository);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void configure(H restHttp) {

        SecurityContextRepository securityContextRepository = restHttp
                .getSharedObject(SecurityContextRepository.class);
        if (securityContextRepository == null) {
            JwtTokenProvider provider = restHttp.getSharedObject(JwtTokenProvider.class);
            if (provider == null) {
                if (this.providedJwtTokenProvider == null) {
                    provider = new JwtTokenProvider("coffee-jwt", 24 * 60 * 60 * 1000);
                } else {
                    provider = this.providedJwtTokenProvider;
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

    public void setProvidedJwtTokenProvider(JwtTokenProvider providedJwtTokenProvider) {
        this.providedJwtTokenProvider = providedJwtTokenProvider;
    }
}
