package site.zido.coffee.auth.web.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 使用@{@link HttpServletRequest#changeSessionId()} 防止固定会话攻击
 *
 * @author zido
 */
public class DefaultSecuritySessionStrategy extends AbstractSecuritySessionStrategy {
    @Override
    protected HttpSession applySessionFixation(HttpServletRequest request) {
        request.changeSessionId();
        return request.getSession();
    }
}
