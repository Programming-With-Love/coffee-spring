package site.zido.coffee.auth.utils.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.util.UrlPathHelper;
import site.zido.coffee.auth.web.utils.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AntPathRequestMatcherTest {
    @Mock
    private HttpServletRequest request;

    @Test
    public void matchesWhenUrlPathHelperThenMatchesOnRequestUri() {
        AntPathRequestMatcher matcher = new AntPathRequestMatcher("/foo/bar", null, true, new UrlPathHelper());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo/bar");
        assertThat(matcher.matches(request)).isTrue();
    }

    @Test
    public void singleWildcardMatchedAnyPath() {
        AntPathRequestMatcher matcher = new AntPathRequestMatcher("/**");
        assertThat(matcher.getPattern()).isEqualTo("/**");
        assertThat(matcher.matches(createRequest("/blah"))).isTrue();
        matcher = new AntPathRequestMatcher("**");
        assertThat(matcher.matches(createRequest("/blah"))).isTrue();
        assertThat(matcher.matches(createRequest(""))).isTrue();
    }

    @Test
    public void trailingWildcardMatchesCorrectly() {
        AntPathRequestMatcher matcher = new AntPathRequestMatcher("/blah/blAh/**", null,
                false);
        assertThat(matcher.matches(createRequest("/BLAH/blah"))).isTrue();
        assertThat(matcher.matches(createRequest("/blah/bleh"))).isFalse();
        assertThat(matcher.matches(createRequest("/blah/blah/"))).isTrue();
        assertThat(matcher.matches(createRequest("/blah/blah/xxx"))).isTrue();
        assertThat(matcher.matches(createRequest("/blah/blaha"))).isFalse();
        assertThat(matcher.matches(createRequest("/blah/bleh/"))).isFalse();
        MockHttpServletRequest request = createRequest("/blah/");

        request.setPathInfo("blah/bleh");
        assertThat(matcher.matches(request)).isTrue();

        matcher = new AntPathRequestMatcher("/bl?h/blAh/**", null, false);
        assertThat(matcher.matches(createRequest("/BLAH/Blah/aaa/"))).isTrue();
        assertThat(matcher.matches(createRequest("/bleh/Blah"))).isTrue();

        matcher = new AntPathRequestMatcher("/blAh/**/blah/**", null, false);
        assertThat(matcher.matches(createRequest("/blah/blah"))).isTrue();
        assertThat(matcher.matches(createRequest("/blah/bleh"))).isFalse();
        assertThat(matcher.matches(createRequest("/blah/aaa/blah/bbb"))).isTrue();
    }

    @Test
    public void trailingWildcardWithVariableMatchesCorrectly() {
        AntPathRequestMatcher matcher = new AntPathRequestMatcher("/{id}/blAh/**", null,
                false);
        assertThat(matcher.matches(createRequest("/1234/blah"))).isTrue();
        assertThat(matcher.matches(createRequest("/4567/bleh"))).isFalse();
        assertThat(matcher.matches(createRequest("/paskos/blah/"))).isTrue();
        assertThat(matcher.matches(createRequest("/12345/blah/xxx"))).isTrue();
        assertThat(matcher.matches(createRequest("/12345/blaha"))).isFalse();
        assertThat(matcher.matches(createRequest("/paskos/bleh/"))).isFalse();
    }

    private HttpServletRequest createRequestWithNullMethod(String path) {
        when(this.request.getServletPath()).thenReturn(path);
        return this.request;
    }

    private MockHttpServletRequest createRequest(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setQueryString("doesntMatter");
        request.setServletPath(path);
        request.setMethod("POST");

        return request;
    }
}
