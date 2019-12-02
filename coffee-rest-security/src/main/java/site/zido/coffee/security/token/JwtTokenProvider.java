package site.zido.coffee.security.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.Date;

/**
 * 使用jwt 实现的token提供者
 *
 * @author zido
 */
public class JwtTokenProvider implements TokenProvider, InitializingBean {

    private String jwtSecret;

    private int jwtExpirationInMs;

    private UserDetailsService userService;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    public JwtTokenProvider(String jwtSecret, int jwtExpirationInMs) {
        this.jwtSecret = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public void setUserService(UserDetailsService userService) {
        this.userService = userService;
    }

    @Override
    public String generate(SecurityContext subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(subject.getAuthentication().getName())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    @Override
    public SecurityContext parse(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        String username = claims.getSubject();
        if (username == null) {
            return null;
        }
        UserDetails user = userService.loadUserByUsername(username);
        SecurityContextImpl context = new SecurityContextImpl();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null,
                authoritiesMapper.mapAuthorities(user.getAuthorities()));
        context.setAuthentication(authenticationToken);
        return context;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(userService, "User Service Cannot be null");
    }
}
