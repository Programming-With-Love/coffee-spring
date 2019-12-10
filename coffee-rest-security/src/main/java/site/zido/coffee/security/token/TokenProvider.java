package site.zido.coffee.security.token;

import org.springframework.security.core.context.SecurityContext;

import javax.servlet.http.HttpServletResponse;

/**
 * token 提供者
 *
 * @author zido
 */
public interface TokenProvider {
    /**
     * 生成token
     *
     * @param subject 认证体
     * @return token string
     */
    String generate(SecurityContext subject);

    /**
     * 解析token
     *
     * @param token    token
     * @param response response,例如jwt等会需要response对象进行续期操作
     * @return json字符串
     */
    SecurityContext parse(String token, HttpServletResponse response) throws TokenInvalidException;
}
