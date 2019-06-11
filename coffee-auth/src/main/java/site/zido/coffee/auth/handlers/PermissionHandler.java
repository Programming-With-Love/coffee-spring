package site.zido.coffee.auth.handlers;

import javax.servlet.http.HttpServletRequest;

public interface PermissionHandler {
    Object determinerUser(String role, HttpServletRequest request);

    Object determinePermissions(String permission, HttpServletRequest req);
}
