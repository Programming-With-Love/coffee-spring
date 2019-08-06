package site.zido.coffee.auth.web.utils.matcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 提取URI变量的接口。
 *
 * @author zido
 */
public interface RequestVariablesExtractor {
    /**
     * 提取url 变量
     *
     * @param request request
     * @return map
     */
    Map<String, String> extract(HttpServletRequest request);
}
