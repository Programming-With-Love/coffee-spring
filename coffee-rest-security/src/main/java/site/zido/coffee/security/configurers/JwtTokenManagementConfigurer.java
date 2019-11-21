package site.zido.coffee.security.configurers;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.web.context.SecurityContextRepository;
import site.zido.coffee.security.jwt.JwtSecurityContextRepository;
import site.zido.coffee.security.jwt.JwtTokenProvider;

/**
 * @author zido
 */
public class JwtTokenManagementConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractHttpConfigurer<SessionManagementConfigurer<H>, H> {
    private JwtTokenProvider providedJwtTokenProvider;

    public JwtTokenManagementConfigurer() {
    }

    @Override
    public void init(H restHttp) throws Exception {
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
            JwtSecurityContextRepository repository = new JwtSecurityContextRepository(provider);
            AuthenticationTrustResolver trustResolver = restHttp
                    .getSharedObject(AuthenticationTrustResolver.class);
            if (trustResolver != null) {
                repository.setTrustResolver(trustResolver);
            }
            restHttp.setSharedObject(SecurityContextRepository.class,
                    repository);
        }
    }

    public void setProvidedJwtTokenProvider(JwtTokenProvider providedJwtTokenProvider) {
        this.providedJwtTokenProvider = providedJwtTokenProvider;
    }

}
