package site.zido.coffee.auth.web.utils.matcher;

import javax.servlet.http.HttpServletRequest;

public class AnyRequestMatcher implements RequestMatcher {
    public static final RequestMatcher INSTANCE = new AnyRequestMatcher();

    @Override
    public boolean matches(HttpServletRequest request) {
        return true;
    }

    public boolean equals(Object obj) {
        return obj instanceof AnyRequestMatcher;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "any request";
    }

    private AnyRequestMatcher() {
    }
}
