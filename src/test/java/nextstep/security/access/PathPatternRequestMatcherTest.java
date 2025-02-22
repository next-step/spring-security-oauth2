package nextstep.security.access;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class PathPatternRequestMatcherTest {

    @DisplayName("PathPatternRequestMatcher 는 httpMethod 를 검증한다")
    @ParameterizedTest
    @ValueSource(strings = {"DELETE", "POST", "PATCH","PUT"})
    void httpMethod(String ignoreHttpMethod)  {
        // given
        final String url = "/users";
        final HttpMethod allowHttpMethod = HttpMethod.GET;

        MockHttpServletRequest allowRequest = new MockHttpServletRequest(allowHttpMethod.toString(), url);
        MockHttpServletRequest ignoreRequest = new MockHttpServletRequest(ignoreHttpMethod, url);

        // when
        PathPatternRequestMatcher matcher = new PathPatternRequestMatcher(allowHttpMethod, url);

        // then
        assertSoftly(it -> {
            it.assertThat(matcher.matches(allowRequest)).isTrue();
            it.assertThat(matcher.matches(ignoreRequest)).isFalse();
        });
    }

    @DisplayName("PathPatternRequestMatcher 는 httpMethod 를 uri을 검증한다")
    @Test
    void uri() {
        // given
        final String allowUrl = "/users";
        final HttpMethod httpMethod = HttpMethod.GET;

        MockHttpServletRequest allowRequest = new MockHttpServletRequest(httpMethod.toString(), allowUrl);
        MockHttpServletRequest ignoreRequest = new MockHttpServletRequest(httpMethod.toString(), "/ignoreUrl");

        // when
        PathPatternRequestMatcher matcher = new PathPatternRequestMatcher(httpMethod, allowUrl);

        // then
        assertSoftly(it -> {
            it.assertThat(matcher.matches(allowRequest)).isTrue();
            it.assertThat(matcher.matches(ignoreRequest)).isFalse();
        });
    }

    @DisplayName("PathPatternRequestMatcher 는 pattern url을 검증한다")
    @Test
    void patternUrl() {
        final String allowUrl = "/users/**";
        final HttpMethod httpMethod = HttpMethod.GET;

        PathPatternRequestMatcher matcher = new PathPatternRequestMatcher(httpMethod, allowUrl);

        assertSoftly(it -> {
            it.assertThat(matcher.matches(new MockHttpServletRequest(httpMethod.toString(), "/users"))).isTrue();
            it.assertThat(matcher.matches(new MockHttpServletRequest(httpMethod.toString(), "/users/a"))).isTrue();
            it.assertThat(matcher.matches(new MockHttpServletRequest(httpMethod.toString(), "/users/b/a"))).isTrue();
            it.assertThat(matcher.matches(new MockHttpServletRequest(httpMethod.toString(), "/user"))).isFalse();
        });
    }

    @DisplayName("PathPatternRequestMatcher 는 pathVariable을 가져온다")
    @Test
    void getPathVariable() {
        final String allowUrl = "/users/{username}";
        final String username = "이름";
        final HttpMethod httpMethod = HttpMethod.GET;
        MockHttpServletRequest allowRequest = new MockHttpServletRequest(httpMethod.toString(), "/users/" + username);

        final PathPatternRequestMatcher pathPatternRequestMatcher = new PathPatternRequestMatcher(httpMethod, allowUrl);


        assertThat(pathPatternRequestMatcher.getPathVariable(allowRequest, "username")).isEqualTo(username);
    }

    @DisplayName("매칭되는 주소가 없으면 pathVariable는 null 이다")
    @Test
    void getPathVariableNull() {
        final String allowUrl = "/users/{username}";
        final HttpMethod httpMethod = HttpMethod.GET;
        MockHttpServletRequest request = new MockHttpServletRequest(httpMethod.toString(), "/no/name");

        final PathPatternRequestMatcher pathPatternRequestMatcher = new PathPatternRequestMatcher(httpMethod, allowUrl);

        assertSoftly(it -> {
            it.assertThat(pathPatternRequestMatcher.matches(request)).isFalse();
            it.assertThat(pathPatternRequestMatcher.getPathVariable(request, "username")).isNull();
        });
    }

    @DisplayName("매칭되는 pathVariable가 없으면 null을 반환한다")
    @Test
    void matchedPathVariableNull() {
        final String allowUrl = "/users/{username}";
        final HttpMethod httpMethod = HttpMethod.GET;
        MockHttpServletRequest request = new MockHttpServletRequest(httpMethod.toString(), "/users/name");

        final PathPatternRequestMatcher pathPatternRequestMatcher = new PathPatternRequestMatcher(httpMethod, allowUrl);

        assertSoftly(it -> {
            it.assertThat(pathPatternRequestMatcher.matches(request)).isTrue();
            it.assertThat(pathPatternRequestMatcher.getPathVariable(request, "nomatch")).isNull();
        });
    }


}
