package site.zido.coffee.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.util.StringUtils;
import site.zido.coffee.security.authentication.phone.PhoneCodeAuthenticationToken;

import java.util.*;

/**
 * @author zido
 */
public class TokenProvider {

    private String jwtSecret;

    private int jwtExpirationInMs;

    private ObjectMapper mapper;

    public TokenProvider(String jwtSecret, int jwtExpirationInMs) {
        this.jwtSecret = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
        this.jwtExpirationInMs = jwtExpirationInMs;
        mapper = new ObjectMapper();
        ClassLoader loader = getClass().getClassLoader();
        List<Module> modules = SecurityJackson2Modules.getModules(loader);
        mapper.registerModules(modules);
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ClassLoader loader = TokenProvider.class.getClassLoader();
        List<Module> modules = SecurityJackson2Modules.getModules(loader);
        mapper.registerModules(modules);

        SecurityContextImpl context = new SecurityContextImpl();
        AuthUser user = new AuthUser("123", "123", Collections.emptyList());
        user.setPhone("34534");
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(user, "cccc");
        context.setAuthentication(token);
        String json = mapper.writeValueAsString(context);
        System.out.println(json);

        SecurityContext context2 = mapper.readValue(json, SecurityContext.class);
        System.out.println(context2.getAuthentication().getPrincipal());
    }

    public String generateToken(Object subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(subject.toString())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String parseAuthentication(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
