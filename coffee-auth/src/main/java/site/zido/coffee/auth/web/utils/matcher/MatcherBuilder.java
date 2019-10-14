package site.zido.coffee.auth.web.utils.matcher;

import java.util.List;

/**
 * @author zido
 */
@SuppressWarnings("unchecked")
public class MatcherBuilder<T extends MatcherBuilder> {
    private RequestMatcher requestMatcher;

    public RequestMatcher getRequestMatcher() {
        return requestMatcher;
    }

    public T requestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
        return (T) this;
    }

    public T antMatcher(String pattern) {
        requestMatcher(new AntPathRequestMatcher(pattern));
        return (T) this;
    }

    public T anyMatcher() {
        requestMatcher(AnyRequestMatcher.INSTANCE);
        return (T) this;
    }

    public T or(RequestMatcher... requestMatcher) {
        requestMatcher(new OrRequestMatcher(requestMatcher));
        return (T) this;
    }

    public T or(List<RequestMatcher> requestMatchers) {
        requestMatcher(new OrRequestMatcher(requestMatchers));
        return (T) this;
    }

    public T and(RequestMatcher... requestMatchers) {
        requestMatcher(new AndRequestMatcher(requestMatchers));
        return (T) this;
    }

    public T and(List<RequestMatcher> requestMatchers) {
        requestMatcher(new AndRequestMatcher(requestMatchers));
        return (T) this;
    }
}
