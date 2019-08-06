package site.zido.coffee.auth.web.utils.matcher;

import javax.servlet.http.HttpServletRequest;

/**
 * 请求匹配器
 *
 * @author zido
 */
public interface RequestMatcher {
    /**
     * 判断该请求是否匹配
     *
     * @param request request
     * @return true/false
     */
    boolean matches(HttpServletRequest request);
}
