package site.zido.coffee.auth.context;

import site.zido.coffee.auth.entity.IUser;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * 用户管理器
 *
 * @author zido
 */
public interface UserManager {
    /**
     * 获取当前用户
     *
     * @param request request
     * @return user
     */
    IUser getCurrentUser(HttpServletRequest request);

    /**
     * 获取用户的相关角色
     *
     * @param user 用户
     * @return 角色集合
     */
    Collection<String> getRoles(IUser user);

    /**
     * 将认证结果与请求进行绑定
     *
     * @param request    request
     * @param authResult result
     */
    void bindUser(HttpServletRequest request, IUser authResult);

    /**
     * 清楚绑定
     */
    void clear();
}
