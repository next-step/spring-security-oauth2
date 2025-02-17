package nextstep.security.authentication.filter.oauth.google;

import nextstep.app.application.google.GoogleOAuth2AuthenticationRequestResolver;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.authentication.filter.oauth.OAuth2RedirectAuthenticationFilter;
import org.springframework.http.HttpMethod;

public class GoogleOAuth2RedirectAuthenticationFilter extends OAuth2RedirectAuthenticationFilter {

    public GoogleOAuth2RedirectAuthenticationFilter(GoogleOAuth2AuthenticationRequestResolver requestResolver) {
        super(new MvcRequestMatcher(HttpMethod.GET, "/oauth2/authorization/google"), requestResolver);
    }
}
