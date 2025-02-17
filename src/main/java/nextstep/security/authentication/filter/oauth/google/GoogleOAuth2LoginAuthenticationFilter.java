package nextstep.security.authentication.filter.oauth.google;

import nextstep.app.application.OAuth2TokenRequester;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.authentication.filter.oauth.OAuth2LoginAuthenticationFilter;
import org.springframework.http.HttpMethod;

public class GoogleOAuth2LoginAuthenticationFilter extends OAuth2LoginAuthenticationFilter {

    public GoogleOAuth2LoginAuthenticationFilter(OAuth2TokenRequester auth2TokenRequester) {
        super(new MvcRequestMatcher(HttpMethod.GET, "/login/oauth2/code/google"), auth2TokenRequester);
    }
}
