package nextstep.app.application.github;

import nextstep.security.authentication.OAuth2AuthenticationRequestResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GithubOAuth2AuthenticationRequestResolver implements OAuth2AuthenticationRequestResolver {

    private static final String SCOPE = "read:user";

    @Value("${oauth2.github.client-id}")
    private String clientId;
    @Value("${oauth2.github.authorization.request-uri}")
    private String baseRequestUri;
    @Value("${oauth2.github.authorization.redirect-uri}")
    private String redirectUri;

    @Override
    public String resolve() {
        return baseRequestUri
                + "?response_type=" + RESPONSE_TYPE
                + "&client_id=" + clientId
                + "&scope=" + SCOPE
                + "&redirect_uri=" + redirectUri;
    }
}
