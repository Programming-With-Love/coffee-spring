package site.zido.coffee.security.token;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.OnCommittedResponseWrapper;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public class JwtWriterResponse extends OnCommittedResponseWrapper {
    private final long jwtExpirationInMs;
    private final String issue;
    private final String secret;
    private final String authHeaderName;

    public JwtWriterResponse(HttpServletResponse response, long jwtExpirationInMs, String issue, String secret, String authHeaderName) {
        super(response);
        this.jwtExpirationInMs = jwtExpirationInMs;
        this.issue = issue;
        this.secret = secret;
        this.authHeaderName = authHeaderName;
    }

    @Override
    protected void onResponseCommitted() {
        writeToken(SecurityContextHolder.getContext());
        this.disableOnResponseCommitted();
    }

    protected void writeToken(SecurityContext context) {
        if (isDisableOnResponseCommitted()) {
            return;
        }
        if (context.getAuthentication() != null) {
            String newToken = generateNewToken(context);
            addTokenToResponse(getHttpResponse(), newToken);
            doAfterWriteToken(context);
        }
    }

    protected void doAfterWriteToken(SecurityContext context) {

    }

    protected void addTokenToResponse(HttpServletResponse response, String token) {
        response.setHeader(authHeaderName, token);
    }

    protected String generateNewToken(SecurityContext subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        JwtBuilder builder = Jwts.builder();
        //如果开启匿名，则写入匿名token配置
        if (subject instanceof AnonymousAuthenticationToken) {
            builder.claim("role", "anonymous");
        }
        //如果是匿名用户则subject为空字符串
        return builder
                .setSubject(subject.getAuthentication().getName())
                .setIssuedAt(now)
                .setIssuer(issue)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    protected HttpServletResponse getHttpResponse() {
        return (HttpServletResponse) getResponse();
    }
}
