package site.zido.coffee.auth.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import site.zido.coffee.auth.core.Authentication;
import site.zido.coffee.auth.core.CredentialsContainer;

import javax.naming.AuthenticationException;
import java.util.Collections;
import java.util.List;

/**
 * 遍历迭代认证器以进行认证操作
 *
 * @author zido
 */
public class ProviderManager implements AuthenticationManager, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderManager.class);

    private List<AuthenticationProvider> providers;
    private AuthenticationManager parent;
    private boolean eraseCredentialsAfterAuthentication = true;

    public ProviderManager(List<AuthenticationProvider> providers) {
        this(providers, null);
    }

    public ProviderManager(List<AuthenticationProvider> providers, AuthenticationManager parent) {
        Assert.notNull(providers, "providers list cannot be null");
        this.parent = parent;
        this.providers = providers;
        checkState();
    }

    private void checkState() {
        if (parent == null && providers.isEmpty()) {
            throw new IllegalArgumentException(
                    "A parent AuthenticationManager or a list "
                            + "of AuthenticationProviders is required"
            );
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AbstractAuthenticationException {
        Class<? extends Authentication> targetClass = authentication.getClass();
        AbstractAuthenticationException lastException = null;
        Authentication result = null;
        for (AuthenticationProvider provider : getProviders()) {
            if (!provider.supports(targetClass)) {
                continue;
            }
            LOGGER.debug("Authentication attempt using {}", provider.getClass().getName());
            try {
                result = provider.authenticate(authentication);
                if (result != null) {
                    copyDetails(authentication, result);
                    break;
                }
            } catch (AccountStatusException | InternalAuthenticationException e) {
                throw e;
            } catch (AbstractAuthenticationException e) {
                lastException = e;
            }
        }
        if (result == null && parent != null) {
            try {
                result = parent.authenticate(authentication);
            } catch (ProviderNotFoundException ignore) {
                //ignore exception
            } catch (AbstractAuthenticationException e) {
                lastException = e;
            }
        }
        if (result != null) {
            if (eraseCredentialsAfterAuthentication && result instanceof CredentialsContainer) {
                ((CredentialsContainer) result).eraseCredentials();
            }
            return result;
        }
        if (lastException == null) {
            lastException = new ProviderNotFoundException("No AuthenticationProvider found by " + targetClass.getName());
        }
        throw lastException;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        checkState();
    }

    public List<AuthenticationProvider> getProviders() {
        return providers;
    }

    private void copyDetails(Authentication source, Authentication dest) {
        if ((dest instanceof AbstractAuthenticationToken) && (dest.getDetails() == null)) {
            AbstractAuthenticationToken token = (AbstractAuthenticationToken) dest;

            token.setDetails(source.getDetails());
        }
    }

    public boolean isEraseCredentialsAfterAuthentication() {
        return eraseCredentialsAfterAuthentication;
    }

    public void setEraseCredentialsAfterAuthentication(boolean eraseCredentialsAfterAuthentication) {
        this.eraseCredentialsAfterAuthentication = eraseCredentialsAfterAuthentication;
    }
}
