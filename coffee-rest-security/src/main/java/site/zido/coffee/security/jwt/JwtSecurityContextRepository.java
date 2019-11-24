package site.zido.coffee.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author zido
 */
public class JwtSecurityContextRepository implements SecurityContextRepository {
    public static final String DEFAULT_AUTH_HEADER_NAME = "Authorization";
    private static Logger LOGGER = LoggerFactory.getLogger(JwtSecurityContextRepository.class);
    private String authHeaderName = DEFAULT_AUTH_HEADER_NAME;
    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
    private JwtTokenProvider tokenProvider;
    private ObjectMapper mapper;

    public JwtSecurityContextRepository(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
        mapper = new ObjectMapper();
        ClassLoader loader = getClass().getClassLoader();
        List<Module> modules = SecurityJackson2Modules.getModules(loader);
        mapper.registerModules(modules);
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();
        String token = request.getHeader(authHeaderName);
        String json = tokenProvider.getAuthenticationFromJwt(token);
        SecurityContext authentication;
        if (json == null) {
            authentication = generateNewContext();
        } else {
            try {
                authentication = mapper.readValue(json, SecurityContext.class);
            } catch (JsonProcessingException e) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("jwt did not contain a SecurityContext but contained: '"
                            + json
                            + "'; are you improperly modifying the HttpSession directly "
                            + "(you should always use SecurityContextHolder) or using the Authentication attribute "
                            + "reserved for this class?");
                }
                authentication = generateNewContext();
            }
        }

        LOGGER.debug("Obtained a valid SecurityContext from " + authHeaderName
                + " in request header"
                + ": '" + authentication + "'");
        return authentication;
    }

    protected SecurityContext generateNewContext() {
        return SecurityContextHolder.createEmptyContext();
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = context.getAuthentication();
        if (authentication != null && !trustResolver.isAnonymous(authentication)) {
            try {
                String json = mapper.writeValueAsString(context);
                String token = tokenProvider.generateToken(json);
                response.setHeader(authHeaderName, token);
                LOGGER.debug("SecurityContext '" + context
                        + "' stored to response.header: " + authHeaderName);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("cannot write security context as string", e);
            }
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return StringUtils.hasLength(request.getHeader(authHeaderName));
    }

    public void setAuthHeaderName(String authHeaderName) {
        this.authHeaderName = authHeaderName;
    }

    public void setTokenProvider(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public void setTrustResolver(AuthenticationTrustResolver trustResolver) {
        this.trustResolver = trustResolver;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
}
