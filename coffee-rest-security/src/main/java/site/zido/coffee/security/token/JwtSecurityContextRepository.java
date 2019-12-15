package site.zido.coffee.security.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zido
 */
public class JwtSecurityContextRepository implements SecurityContextRepository {
    public static final String DEFAULT_AUTH_HEADER_NAME = "Authorization";
    private static Logger LOGGER = LoggerFactory.getLogger(JwtSecurityContextRepository.class);
    private String authHeaderName = DEFAULT_AUTH_HEADER_NAME;
    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
    private String jwtSecret;

    private long jwtExpirationInMs;

    private String issue = "coffee-security";

    private UserDetailsService userService;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    public JwtSecurityContextRepository() {
    }

    public JwtSecurityContextRepository(String jwtSecret, long jwtExpirationInMs) {
        this.jwtSecret = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();
        HttpServletResponse response = requestResponseHolder.getResponse();
        String token = request.getHeader(authHeaderName);
        SecurityContext context;
        if (token == null) {
            LOGGER.debug("No token currently exists");
            context = generateNewContext();
        } else {
            try {
                context = parseFromToken(token, response);
                if (context == null) {
                    if (LOGGER.isWarnEnabled()) {
                        LOGGER.warn("jwt did not contain a SecurityContext but contained: '"
                                + context
                                + "'; are you improperly modifying the HttpSession directly "
                                + "(you should always use SecurityContextHolder) or using the Authentication attribute "
                                + "reserved for this class?");
                    }
                    context = generateNewContext();
                }
            } catch (TokenInvalidException e) {
                context = generateNewContext();
            }
        }

        LOGGER.debug("Obtained a valid SecurityContext from " + authHeaderName
                + " in request header"
                + ": '" + context + "'");
        return context;
    }

    protected SecurityContext generateNewContext() {
        return SecurityContextHolder.createEmptyContext();
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return StringUtils.hasLength(request.getHeader(authHeaderName));
    }

    public void setAuthHeaderName(String authHeaderName) {
        this.authHeaderName = authHeaderName;
    }

    public void setTrustResolver(AuthenticationTrustResolver trustResolver) {
        this.trustResolver = trustResolver;
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

    private String generateNewToken(SecurityContext subject) {
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

    public SecurityContext parseFromToken(String token, HttpServletResponse response) throws TokenInvalidException {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException | UnsupportedJwtException e) {
            throw new TokenInvalidException("token失效", e);
        } catch (MalformedJwtException e) {
            LOGGER.warn("jwt token被修改过:{}", token);
            throw new TokenInvalidException("token失效", e);
        } catch (SignatureException e) {
            LOGGER.warn("签名异常:{}", token);
            throw new TokenInvalidException("token失效", e);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("token串非法:{}", token);
            throw new TokenInvalidException("token失效", e);
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
            String newToken = generateNewToken(context);
            response.setHeader("Authorization", newToken);
        }
        return context;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }
}
