package site.zido.coffee.security;

import org.springframework.security.core.context.SecurityContext;

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
     * @param token token
     * @return json字符串
     */
    SecurityContext parse(String token);
}
