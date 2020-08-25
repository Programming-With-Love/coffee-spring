package site.zido.coffee.security.token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.context.SecurityContext;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public class RefreshWriterResponse extends JwtWriterResponse {
    private long refreshTokenExpirationInMs;
    private String issue;
    private String refreshSecret;
    private String refreshHeaderName;

    public RefreshWriterResponse(HttpServletResponse response,
                                 long jwtExpirationInMs,
                                 String issue,
                                 String jwtSecret,
                                 String authHeaderName,
                                 long refreshTokenExpirationInMs,
                                 String refreshSecret) {
        super(response, jwtExpirationInMs, issue, jwtSecret, authHeaderName);
        this.refreshTokenExpirationInMs = refreshTokenExpirationInMs;
        this.refreshSecret = refreshSecret;
    }

    public RefreshWriterResponse(HttpServletResponse response,
                                 long jwtExpirationInMs,
                                 String issue,
                                 String jwtSecret,
                                 String authHeaderName,
                                 long refreshTokenExpirationInMs) {
        super(response, jwtExpirationInMs, issue, jwtSecret, authHeaderName);
        this.refreshTokenExpirationInMs = refreshTokenExpirationInMs;
        this.refreshSecret = jwtSecret;
    }

    protected String generateRefreshToken(SecurityContext subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationInMs);

        return Jwts.builder()
                .setSubject(subject.getAuthentication().getName())
                .setIssuedAt(now)
                .setIssuer(issue)
                .setExpiration(expiryDate)
                .claim("scope", "refresh")
                .signWith(SignatureAlgorithm.HS512, refreshSecret)
                .compact();
    }

    @Override
    protected void doAfterWriteToken(SecurityContext context) {
        getHttpResponse().setHeader(refreshHeaderName, generateRefreshToken(context));
    }
}
