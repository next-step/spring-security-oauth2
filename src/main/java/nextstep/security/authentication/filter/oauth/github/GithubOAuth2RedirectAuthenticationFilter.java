package nextstep.security.authentication.filter.oauth.github;

import nextstep.app.application.github.GithubOAuth2AuthenticationRequestResolver;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.authentication.filter.oauth.OAuth2RedirectAuthenticationFilter;
import org.springframework.http.HttpMethod;

public class GithubOAuth2RedirectAuthenticationFilter extends OAuth2RedirectAuthenticationFilter {

    public GithubOAuth2RedirectAuthenticationFilter(GithubOAuth2AuthenticationRequestResolver requestResolver) {
        super(new MvcRequestMatcher(HttpMethod.GET, "/oauth2/authorization/github"), requestResolver);
    }
}
