package site.zido.coffee.security.token;

/**
 * jwt token相关属性
 */
public class JwtTokenProperties {
    private long jwtExpirationInMs;
    private String issue;
    private String secret;
    private String authHeaderName;
    private JwtRefreshProperties refreshment;

    public long getJwtExpirationInMs() {
        return jwtExpirationInMs;
    }

    public void setJwtExpirationInMs(long jwtExpirationInMs) {
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getAuthHeaderName() {
        return authHeaderName;
    }

    public void setAuthHeaderName(String authHeaderName) {
        this.authHeaderName = authHeaderName;
    }

    public JwtRefreshProperties getRefreshment() {
        return refreshment;
    }

    public void setRefreshment(JwtRefreshProperties refreshment) {
        this.refreshment = refreshment;
    }

}
