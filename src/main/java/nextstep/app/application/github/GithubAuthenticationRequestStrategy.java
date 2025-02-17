package nextstep.app.application.github;

import nextstep.security.authentication.OAuth2AuthenticationRequestStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GithubAuthenticationRequestStrategy implements OAuth2AuthenticationRequestStrategy {
    private static final String SCOPE = "read:user";
    
    @Value("${oauth2.github.client-id}")
    private String clientId;
    @Value("${oauth2.github.authorization.request-uri}")
    private String baseRequestUri;
    @Value("${oauth2.github.authorization.redirect-uri}")
    private String redirectUri;

    @Override
    public String getOAuth2Type() {
        return "github";
    }

    @Override
    public String getScope() {
        return SCOPE;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getBaseRequestUri() {
        return baseRequestUri;
    }

    @Override
    public String getRedirectUri() {
        return redirectUri;
    }
}
