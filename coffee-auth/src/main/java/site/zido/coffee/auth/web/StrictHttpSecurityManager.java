package site.zido.coffee.auth.web;

import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.util.*;

public class StrictHttpSecurityManager implements HttpSecurityManager {
    /**
     * Used to specify to {@link #setAllowedHttpMethods(Collection)} that any HTTP method should be allowed.
     */
    private static final Set<String> ALLOW_ANY_HTTP_METHOD = Collections.unmodifiableSet(Collections.emptySet());

    private static final String ENCODED_PERCENT = "%25";

    private static final String PERCENT = "%";

    private static final List<String> FORBIDDEN_ENCODED_PERIOD = Collections.unmodifiableList(Arrays.asList("%2e", "%2E"));

    private static final List<String> FORBIDDEN_SEMICOLON = Collections.unmodifiableList(Arrays.asList(";", "%3b", "%3B"));

    private static final List<String> FORBIDDEN_FORWARDSLASH = Collections.unmodifiableList(Arrays.asList("%2f", "%2F"));

    private static final List<String> FORBIDDEN_BACKSLASH = Collections.unmodifiableList(Arrays.asList("\\", "%5c", "%5C"));

    private Set<String> encodedUrlBlacklist = new HashSet<String>();

    private Set<String> decodedUrlBlacklist = new HashSet<String>();

    private Set<String> allowedHttpMethods = createDefaultAllowedHttpMethods();

    public StrictHttpSecurityManager() {
        urlBlacklistsAddAll(FORBIDDEN_SEMICOLON);
        urlBlacklistsAddAll(FORBIDDEN_FORWARDSLASH);
        urlBlacklistsAddAll(FORBIDDEN_BACKSLASH);

        this.encodedUrlBlacklist.add(ENCODED_PERCENT);
        this.encodedUrlBlacklist.addAll(FORBIDDEN_ENCODED_PERIOD);
        this.decodedUrlBlacklist.add(PERCENT);
    }

    public void setUnsafeAllowAnyHttpMethod(boolean unsafeAllowAnyHttpMethod) {
        this.allowedHttpMethods = unsafeAllowAnyHttpMethod ? ALLOW_ANY_HTTP_METHOD : createDefaultAllowedHttpMethods();
    }

    private void urlBlacklistsAddAll(Collection<String> values) {
        this.encodedUrlBlacklist.addAll(values);
        this.decodedUrlBlacklist.addAll(values);
    }

    public void setAllowedHttpMethods(Collection<String> allowedHttpMethods) {
        if (allowedHttpMethods == null) {
            throw new IllegalArgumentException("allowedHttpMethods cannot be null");
        }
        if (allowedHttpMethods == ALLOW_ANY_HTTP_METHOD) {
            this.allowedHttpMethods = ALLOW_ANY_HTTP_METHOD;
        } else {
            this.allowedHttpMethods = new HashSet<>(allowedHttpMethods);
        }
    }

    private static Set<String> createDefaultAllowedHttpMethods() {
        Set<String> result = new HashSet<>();
        result.add(HttpMethod.DELETE.name());
        result.add(HttpMethod.GET.name());
        result.add(HttpMethod.HEAD.name());
        result.add(HttpMethod.OPTIONS.name());
        result.add(HttpMethod.PATCH.name());
        result.add(HttpMethod.POST.name());
        result.add(HttpMethod.PUT.name());
        return result;
    }

    @Override
    public HttpServletRequest getSecurityRequest(HttpServletRequest request) throws RequestRejectedException {
        rejectForbiddenHttpMethod(request);
        rejectBlacklistedUrls(request);
        if (!isNormalized(request)) {
            throw new RequestRejectedException("The request was rejected because the URL was not normalized.");
        }
        String requestURI = request.getRequestURI();
        if (!containsOnlyPrintableAsciiCharacters(requestURI)) {
            throw new RequestRejectedException("The requestURI was rejected because it can only contain printable ASCII characters.");
        }
        return new HttpServletRequestWrapper(request);
    }

    private static boolean containsOnlyPrintableAsciiCharacters(String uri) {
        int length = uri.length();
        for (int i = 0; i < length; i++) {
            char c = uri.charAt(i);
            if (c < '\u0020' || c > '\u007e') {
                return false;
            }
        }

        return true;
    }

    private static boolean isNormalized(HttpServletRequest request) {
        if (!isNormalized(request.getRequestURI())) {
            return false;
        }
        if (!isNormalized(request.getContextPath())) {
            return false;
        }
        if (!isNormalized(request.getServletPath())) {
            return false;
        }
        if (!isNormalized(request.getPathInfo())) {
            return false;
        }
        return true;
    }

    @Override
    public HttpServletResponse getSecurityResponse(HttpServletResponse response) {
        return new SecurityHttpServletResponse(response);
    }

    private void rejectForbiddenHttpMethod(HttpServletRequest request) throws RequestRejectedException {
        if (this.allowedHttpMethods == ALLOW_ANY_HTTP_METHOD) {
            return;
        }
        if (!this.allowedHttpMethods.contains(request.getMethod())) {
            throw new RequestRejectedException("the request was rejected because the http method \""
                    + request
                    + "\" was forbidden");
        }
    }

    private void rejectBlacklistedUrls(HttpServletRequest request) {
        for (String blackUrl : encodedUrlBlacklist) {
            if (encodedUrlContains(request, blackUrl)) {
                throw new RequestRejectedException("The request was rejected because the URL contained a potentially malicious String \"" + blackUrl + "\"");
            }
        }
        for (String blackUrl : decodedUrlBlacklist) {
            if (decodedUrlContains(request, blackUrl)) {
                throw new RequestRejectedException("The request was rejected because the URL contained a potentially malicious String \"" + blackUrl + "\"");
            }
        }
    }

    private static boolean decodedUrlContains(HttpServletRequest request, String value) {
        if (valueContains(request.getServletPath(), value)) {
            return true;
        }
        if (valueContains(request.getPathInfo(), value)) {
            return true;
        }
        return false;
    }

    private static boolean encodedUrlContains(HttpServletRequest request, String value) {
        if (valueContains(request.getContextPath(), value)) {
            return true;
        }
        return valueContains(request.getRequestURI(), value);
    }

    private static boolean valueContains(String value, String contains) {
        return value != null && value.contains(contains);
    }

    private static boolean isNormalized(String path) {
        if (path == null) {
            return true;
        }
        if (path.contains("//")) {
            return false;
        }
        for (int i = path.length(); i > 0; ) {
            int j = path.lastIndexOf('/', i - 1);
            int gap = i - j;
            if (gap == 2 && path.charAt(j + 1) == '.') {
                return false;
            } else if (gap == 3 && path.charAt(j + 1) == '.' && path.charAt(j + 2) == '.') {
                return false;
            }
            i = j;
        }
        return true;
    }
}
