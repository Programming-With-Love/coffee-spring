package site.zido.coffee.auth.web;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 过滤器链的管理器
 * <p>
 * 负责为请求@{@link HttpServletRequest}找到能够与匹配的过滤器链
 *
 * @author zido
 */
public interface FilterChainManager {
    /**
     * 该请求是否与当前的过滤器链匹配
     *
     * @param request 请求
     * @return true/false
     */
    boolean matches(HttpServletRequest request);

    /**
     * 获取filters
     *
     * @return filters
     */
    List<Filter> getFilters();
}
