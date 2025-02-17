package nextstep.security.authentication.filter.oauth.github;

import nextstep.app.application.OAuth2TokenRequester;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.authentication.filter.oauth.OAuth2LoginAuthenticationFilter;
import org.springframework.http.HttpMethod;

public class GithubOAuth2LoginAuthenticationFilter extends OAuth2LoginAuthenticationFilter {

    public GithubOAuth2LoginAuthenticationFilter(OAuth2TokenRequester auth2TokenRequester) {
        super(new MvcRequestMatcher(HttpMethod.GET, "/login/oauth2/code/github"), auth2TokenRequester);
    }
}
