package site.zido.coffee.auth.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 未登录处理器
 *
 * @author zido
 */
public interface LoginExpectedHandler {
    /**
     * 处理未登录时的情况
     *
     * @param request  request
     * @param response response
     * @throws IOException ex
     */
    void handle(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
