package site.zido.coffee.auth.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserContextRepository {
    UserContext loadContext(HttpServletRequest request, HttpServletResponse response);

    void saveContext(UserContext context, HttpServletRequest request, HttpServletResponse response);

    boolean containsContext(HttpServletRequest request);
}
