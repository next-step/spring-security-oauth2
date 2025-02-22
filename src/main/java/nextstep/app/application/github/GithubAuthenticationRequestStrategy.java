package nextstep.app.application.github;

import nextstep.security.authentication.oauth.OAuth2AuthenticationRequestStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GithubAuthenticationRequestStrategy implements OAuth2AuthenticationRequestStrategy {

    @Value("${oauth2.github.authorization.request-uri}")
    private String baseRequestUri;
    @Value("${oauth2.github.registration-id}")
    private String registrationId;

    @Override
    public String getRegistrationId() {
        return registrationId;
    }

    @Override
    public String getBaseRequestUri() {
        return baseRequestUri;
    }
}
