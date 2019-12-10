package site.zido.coffee.security.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

/**
 * 使用jwt 实现的token提供者
 *
 * @author zido
 */
public class JwtTokenProvider implements TokenProvider, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

    private String jwtSecret;

    private long jwtExpirationInMs;

    private String issue = "coffee-security";

    private UserDetailsService userService;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    public JwtTokenProvider() {
    }

    public JwtTokenProvider(String jwtSecret, long jwtExpirationInMs) {
        this.jwtSecret = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public void setUserService(UserDetailsService userService) {
        this.userService = userService;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
    }

    public void setJwtExpirationInMs(long jwtExpirationInMs) {
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public void setAuthoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper) {
        this.authoritiesMapper = authoritiesMapper;
    }

    @Override
    public String generate(SecurityContext subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (long) (jwtExpirationInMs * 1.5));

        return Jwts.builder()
                .setSubject(subject.getAuthentication().getName())
                .setIssuedAt(now)
                .setIssuer(issue)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    @Override
    public SecurityContext parse(String token, HttpServletResponse response) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return null;
        } catch (UnsupportedJwtException e) {
            LOGGER.warn("不支持的jwt token:{}", token);
            return null;
        } catch (MalformedJwtException e) {
            LOGGER.warn("jwt token被修改过:{}", token);
            return null;
        } catch (SignatureException e) {
            LOGGER.warn("签名异常:{}", token);
            return null;
        } catch (IllegalArgumentException e) {
            LOGGER.warn("token串非法:{}", token);
            return null;
        }
        String username = claims.getSubject();
        if (username == null) {
            return null;
        }

        UserDetails user = userService.loadUserByUsername(username);
        SecurityContextImpl context = new SecurityContextImpl();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null,
                authoritiesMapper.mapAuthorities(user.getAuthorities()));
        context.setAuthentication(authenticationToken);
        Date issued = claims.getIssuedAt();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(issued);
        calendar.add(Calendar.MILLISECOND, (int) (this.jwtExpirationInMs * 0.5));
        if (calendar.getTime().before(new Date())) {
            String newToken = generate(context);
            response.setHeader("Authorization", newToken);
        }
        return context;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(userService, "User Service Cannot be null");
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }
}
