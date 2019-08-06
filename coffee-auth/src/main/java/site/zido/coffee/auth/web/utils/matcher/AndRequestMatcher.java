package site.zido.coffee.auth.web.utils.matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @author zido
 */
public class AndRequestMatcher implements RequestMatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(AndRequestMatcher.class);
    private final List<RequestMatcher> requestMatchers;

    public AndRequestMatcher(List<RequestMatcher> requestMatchers) {
        Assert.notEmpty(requestMatchers, "requestMatchers can't be null or empty");
        if (requestMatchers.contains(null)) {
            throw new IllegalArgumentException("requestMatchers cannot contain null values");
        }
        this.requestMatchers = requestMatchers;
    }

    public AndRequestMatcher(RequestMatcher... requestMatchers) {
        this(Arrays.asList(requestMatchers));
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        for (RequestMatcher requestMatcher : requestMatchers) {
            LOGGER.debug("try to match using {}", requestMatcher.toString());
            if (!requestMatcher.matches(request)) {
                LOGGER.debug("did not match");
                return false;
            }
        }
        LOGGER.debug("All requestMatchers returned true");
        return true;
    }

    @Override
    public String toString() {
        return "AndRequestMatcher [requestMatchers=" + requestMatchers + "]";
    }
}
