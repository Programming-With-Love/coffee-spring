package site.zido.coffee.security.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtRefreshFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtRefreshFilter.class);
    private static final String DEFAULT_REFRESH_HEADER = "refresh-token";

    private String refreshHeader = DEFAULT_REFRESH_HEADER;
    private RequestMatcher requestMatcher = new AntPathRequestMatcher("/token/refresh");
    private boolean postOnly = true;
    private String refreshSecret;
    private UserDetailsService userService;
    private GrantedAuthoritiesMapper authoritiesMapper;

    private long jwtExpirationInMs;
    private String issue;
    private String jwtSecret;
    private String authHeaderName;
    private long refreshTokenExpirationInMs;

    public JwtRefreshFilter() {
    }

    public void setProcessUrl(String processUrl) {
        this.requestMatcher = new AntPathRequestMatcher(processUrl);
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!requireRefresh(request)) {
            String token = request.getHeader(authHeaderName);
            boolean needAddRefreshToken = false;
            if (StringUtils.hasText(token)) {

            }
            filterChain.doFilter(request, response);
            return;
//            String token = response.getHeader(authHeaderName);
        }
        if ((postOnly && !"POST".equalsIgnoreCase(request.getMethod()))) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }
        String refreshToken = request.getHeader(refreshHeader);
        if (!StringUtils.hasText(refreshToken)) {
            throw new TokenInvalidException("refreshToken is empty");
        }
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(refreshSecret)
                    .parseClaimsJwt(refreshToken)
                    .getBody();
        } catch (ExpiredJwtException | UnsupportedJwtException e) {
            throw new TokenInvalidException("token失效", e);
        } catch (MalformedJwtException e) {
            LOGGER.warn("refresh token被修改过:{}", refreshToken);
            throw new TokenInvalidException("token失效", e);
        } catch (SignatureException e) {
            LOGGER.warn("refresh token签名异常:{}", refreshToken);
            throw new TokenInvalidException("token失效", e);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("refresh token串非法:{}", refreshToken);
            throw new TokenInvalidException("token失效", e);
        }
        if (!"refresh".equals(claims.get("scope", String.class))) {
            throw new TokenInvalidException("非refreshToken");
        }

        String username = claims.getSubject();
        UserDetails user = userService.loadUserByUsername(username);

        SecurityContext context = new SecurityContextImpl();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null,
                authoritiesMapper.mapAuthorities(user.getAuthorities()));
        context.setAuthentication(authenticationToken);

        new RefreshWriterResponse(response,
                jwtExpirationInMs,
                issue,
                jwtSecret,
                authHeaderName,
                refreshTokenExpirationInMs,
                refreshSecret).writeToken(context);
    }

    protected boolean requireRefresh(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && requestMatcher.matches(request);
    }

    public void setRefreshSecret(String refreshSecret) {
        this.refreshSecret = refreshSecret;
    }

    public void setUserService(UserDetailsService userService) {
        this.userService = userService;
    }

    public void setAuthoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper) {
        this.authoritiesMapper = authoritiesMapper;
    }

    public void setRefreshHeader(String refreshHeader) {
        this.refreshHeader = refreshHeader;
    }

    @Override
    protected String getFilterName() {
        return "JwtRefreshFilter";
    }

    public static String getAppliedName() {
        return "JwtRefreshFilter" + ALREADY_FILTERED_SUFFIX;
    }

    public void setRequestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }

    public void setJwtExpirationInMs(long jwtExpirationInMs) {
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public void setAuthHeaderName(String authHeaderName) {
        this.authHeaderName = authHeaderName;
    }

    public void setRefreshTokenExpirationInMs(long refreshTokenExpirationInMs) {
        this.refreshTokenExpirationInMs = refreshTokenExpirationInMs;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        Assert.hasText(refreshSecret, "refresh secret cannot be null or empty");
        Assert.notNull(userService, "user details service cannot be null");
    }
}
