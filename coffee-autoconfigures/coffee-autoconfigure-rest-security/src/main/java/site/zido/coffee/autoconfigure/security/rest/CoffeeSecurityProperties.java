package site.zido.coffee.autoconfigure.security.rest;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security")
public class CoffeeSecurityProperties {
    private SecureStoreType secureStoreType = SecureStoreType.JWT;
    private JwtProperties jwt = new JwtProperties();
    private Boolean phoneCodeEnable = true;
    private AuthorizationPhoneCodeProperties phoneCode = new AuthorizationPhoneCodeProperties();

    public SecureStoreType getSecureStoreType() {
        return secureStoreType;
    }

    public void setSecureStoreType(SecureStoreType secureStoreType) {
        this.secureStoreType = secureStoreType;
    }

    public JwtProperties getJwt() {
        return jwt;
    }

    public void setJwt(JwtProperties jwt) {
        this.jwt = jwt;
    }

    public Boolean getPhoneCodeEnable() {
        return phoneCodeEnable;
    }

    public void setPhoneCodeEnable(Boolean phoneCodeEnable) {
        this.phoneCodeEnable = phoneCodeEnable;
    }

    public AuthorizationPhoneCodeProperties getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(AuthorizationPhoneCodeProperties phoneCode) {
        this.phoneCode = phoneCode;
    }

    public static class JwtProperties {
        private Boolean refreshSupport = false;
        private String refreshHeader = "Refresh-Token";
        private String refreshSecret;
        private Boolean autoRefresh = true;
        private String secret;
        private String header = "Authorization";
        private Long renewInMs = (long) (10 * 60 * 1000);
        private Long expiration = (long) (3600 * 1000);

        public Boolean getRefreshSupport() {
            return refreshSupport;
        }

        public void setRefreshSupport(Boolean refreshSupport) {
            this.refreshSupport = refreshSupport;
        }

        public Long getRenewInMs() {
            return renewInMs;
        }

        public void setRenewInMs(Long renewInMs) {
            this.renewInMs = renewInMs;
        }

        public Long getExpiration() {
            return expiration;
        }

        public void setExpiration(Long expiration) {
            this.expiration = expiration;
        }

        public Boolean getAutoRefresh() {
            return autoRefresh;
        }

        public void setAutoRefresh(Boolean autoRefresh) {
            this.autoRefresh = autoRefresh;
        }

        public String getRefreshHeader() {
            return refreshHeader;
        }

        public void setRefreshHeader(String refreshHeader) {
            this.refreshHeader = refreshHeader;
        }

        public String getRefreshSecret() {
            return refreshSecret;
        }

        public void setRefreshSecret(String refreshSecret) {
            this.refreshSecret = refreshSecret;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }
    }

    public static class AuthorizationPhoneCodeProperties {
        private String keyPrefix;
        private Long timeout = (long) 60;
        private String processUrl;
        private String codeProcessUrl;

        public String getKeyPrefix() {
            return keyPrefix;
        }

        public void setKeyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        public Long getTimeout() {
            return timeout;
        }

        public void setTimeout(Long timeout) {
            this.timeout = timeout;
        }

        public String getProcessUrl() {
            return processUrl;
        }

        public void setProcessUrl(String processUrl) {
            this.processUrl = processUrl;
        }

        public String getCodeProcessUrl() {
            return codeProcessUrl;
        }

        public void setCodeProcessUrl(String codeProcessUrl) {
            this.codeProcessUrl = codeProcessUrl;
        }
    }
}
