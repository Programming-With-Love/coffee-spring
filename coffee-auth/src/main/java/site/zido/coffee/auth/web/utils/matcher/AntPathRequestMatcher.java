package site.zido.coffee.auth.web.utils.matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

/**
 * ant 风格路径匹配
 *
 * @author zido
 */
public class AntPathRequestMatcher implements RequestMatcher, RequestVariablesExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AntPathRequestMatcher.class);
    private static final String MATCH_ALL = "/**";
    private static final String MATCH_ALL_2 = "**";

    private final Matcher matcher;
    private final String pattern;
    private final HttpMethod httpMethod;
    private final boolean caseSensitive;

    private final UrlPathHelper urlPathHelper;

    public AntPathRequestMatcher(String pattern) {
        this(pattern, null);
    }

    public AntPathRequestMatcher(String pattern, String httpMethod) {
        this(pattern, httpMethod, true);
    }

    public AntPathRequestMatcher(String pattern, String httpMethod,
                                 boolean caseSensitive) {
        this(pattern, httpMethod, caseSensitive, null);
    }

    public AntPathRequestMatcher(String pattern, String httpMethod, boolean caseSensitive, UrlPathHelper urlPathHelper) {
        Assert.hasText(pattern, "Pattern cannot be null or empty");
        this.caseSensitive = caseSensitive;
        if (pattern.equals(MATCH_ALL) || pattern.equals(MATCH_ALL_2)) {
            pattern = MATCH_ALL;
            this.matcher = null;
        } else {
            if (pattern.endsWith(MATCH_ALL)
                    && (pattern.indexOf('?') == -1
                    && pattern.indexOf('{') == -1
                    && pattern.indexOf('}') == -1)
                    && pattern.indexOf("*") == pattern.length() - 2) {
                this.matcher = new SubpathMatcher(pattern.substring(0, pattern.length() - 3), caseSensitive);
            } else {
                this.matcher = new SpringAntMatcher(pattern, caseSensitive);
            }
        }
        this.pattern = pattern;
        this.httpMethod = StringUtils.hasText(httpMethod) ? HttpMethod.valueOf(httpMethod) : null;
        this.urlPathHelper = urlPathHelper;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (this.httpMethod != null && StringUtils.hasText(request.getMethod())
                && this.httpMethod != valueOf(request.getMethod())) {
            LOGGER.debug("Request '{} {}' doesn't match '{} {}'",
                    request.getMethod(),
                    getRequestPath(request),
                    this.httpMethod,
                    this.pattern);
            return false;
        }
        if (this.pattern.equals(MATCH_ALL)) {
            LOGGER.debug("Request '{}' matched byu universal pattern '/**'", getRequestPath(request));
            return true;
        }
        String url = getRequestPath(request);
        LOGGER.debug("Checking match of request : '{}';against '{}'", url, this.pattern);
        return this.matcher.matches(url);
    }

    private String getRequestPath(HttpServletRequest request) {
        if (this.urlPathHelper != null) {
            return this.urlPathHelper.getPathWithinApplication(request);
        }
        String url = request.getServletPath();

        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            url = StringUtils.hasLength(url) ? url + pathInfo : pathInfo;
        }

        return url;
    }

    private static HttpMethod valueOf(String method) {
        try {
            return HttpMethod.valueOf(method);
        } catch (IllegalArgumentException ignore) {
            //ignore
        }

        return null;
    }

    @Override
    public Map<String, String> extract(HttpServletRequest request) {
        if (this.matcher == null || !matches(request)) {
            return Collections.emptyMap();
        }
        String url = getRequestPath(request);
        return this.matcher.extractUriTemplateVariables(url);
    }

    private interface Matcher {
        boolean matches(String path);

        Map<String, String> extractUriTemplateVariables(String path);
    }

    private static class SpringAntMatcher implements Matcher {
        private final AntPathMatcher antMatcher;

        private final String pattern;

        private SpringAntMatcher(String pattern, boolean caseSensitive) {
            this.pattern = pattern;
            this.antMatcher = createMatcher(caseSensitive);
        }

        @Override
        public boolean matches(String path) {
            return this.antMatcher.match(this.pattern, path);
        }

        @Override
        public Map<String, String> extractUriTemplateVariables(String path) {
            return this.antMatcher.extractUriTemplateVariables(this.pattern, path);
        }

        private static AntPathMatcher createMatcher(boolean caseSensitive) {
            AntPathMatcher matcher = new AntPathMatcher();
            matcher.setTrimTokens(false);
            matcher.setCaseSensitive(caseSensitive);
            return matcher;
        }
    }

    private static class SubpathMatcher implements Matcher {
        private final String subpath;
        private final int length;
        private final boolean caseSensitive;

        private SubpathMatcher(String subpath, boolean caseSensitive) {
            assert !subpath.contains("*");
            this.subpath = caseSensitive ? subpath : subpath.toLowerCase();
            this.length = subpath.length();
            this.caseSensitive = caseSensitive;
        }

        @Override
        public boolean matches(String path) {
            if (!this.caseSensitive) {
                path = path.toLowerCase();
            }
            return path.startsWith(this.subpath)
                    && (path.length() == this.length || path.charAt(this.length) == '/');
        }

        @Override
        public Map<String, String> extractUriTemplateVariables(String path) {
            return Collections.emptyMap();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ant [pattern='").append(this.pattern).append("'");

        if (this.httpMethod != null) {
            sb.append(", ").append(this.httpMethod);
        }

        sb.append("]");

        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = this.pattern != null ? this.pattern.hashCode() : 0;
        result = 31 * result + (this.httpMethod != null ? this.httpMethod.hashCode() : 0);
        result = 31 * result + (this.caseSensitive ? 1231 : 1237);
        return result;
    }
}
