package site.zido.coffee.auth.web.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * url 相关工具类
 *
 * @author zido
 */
public class UrlUtils {
    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_HTTPS = "https";

    public static String buildFullRequestUrl(HttpServletRequest r) {
        return buildFullRequestUrl(r.getScheme(), r.getServerName(), r.getServerPort(),
                r.getRequestURI(), r.getQueryString());
    }

    private static String buildFullRequestUrl(String scheme, String serverName,
                                              int serverPort, String requestURI, String queryString) {
        scheme = scheme.toLowerCase();
        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);
        if (SCHEME_HTTP.equals(scheme)) {
            if (serverPort != 80) {
                url.append(":").append(serverPort);
            }
        } else if (SCHEME_HTTPS.equals(scheme)) {
            if (serverPort != 443) {
                url.append(":").append(serverPort);
            }
        }

        url.append(requestURI);

        if (queryString != null) {
            url.append("?").append(queryString);
        }

        return url.toString();
    }

    public static String buildRequestUrl(HttpServletRequest request) {
        return buildRequestUrl(request.getServletPath(), request.getRequestURI(),
                request.getContextPath(), request.getPathInfo(), request.getQueryString());
    }

    private static String buildRequestUrl(String servletPath, String requestURI,
                                          String contextPath, String pathInfo, String queryString) {
        StringBuilder url = new StringBuilder();
        if (servletPath != null) {
            url.append(servletPath);
            if (pathInfo != null) {
                url.append(pathInfo);
            }
        } else {
            url.append(requestURI.substring(contextPath.length()));
        }
        if (queryString != null) {
            url.append("?").append(queryString);
        }
        return url.toString();
    }

}
