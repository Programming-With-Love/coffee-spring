package site.zido.coffee.security.token;

public class JwtRefreshProperties {
    private long refreshTokenExpirationInMs;
    private String issue;
    private String refreshSecret;

    public long getRefreshTokenExpirationInMs() {
        return refreshTokenExpirationInMs;
    }

    public void setRefreshTokenExpirationInMs(long refreshTokenExpirationInMs) {
        this.refreshTokenExpirationInMs = refreshTokenExpirationInMs;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getRefreshSecret() {
        return refreshSecret;
    }

    public void setRefreshSecret(String refreshSecret) {
        this.refreshSecret = refreshSecret;
    }
}
