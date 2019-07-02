package site.zido.coffee.auth.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户禁用时的处理器
 *
 * @author zido
 */
public interface DisabledUserHandler {
    /**
     * 处理
     *
     * @param request  request
     * @param response response
     * @throws IOException ex
     */
    void handle(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
