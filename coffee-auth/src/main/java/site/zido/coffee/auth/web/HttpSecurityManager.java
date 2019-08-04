package site.zido.coffee.auth.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * http安全管理器
 *
 * @author zido
 */
public interface HttpSecurityManager {
    /**
     * 转换为安全的http请求,用来通过后续认证过滤器链
     *
     * @param request 请求
     * @return 原请求的包装
     * @throws RequestRejectedException 当请求不安全时抛出异常
     */
    HttpServletRequest getSecurityRequest(HttpServletRequest request) throws RequestRejectedException;

    /**
     * 转换为安全的http响应，用来通过后续认证过滤器链
     *
     * @param response 响应
     * @return 原响应体的包装
     */
    HttpServletResponse getSecurityResponse(HttpServletResponse response);
}
