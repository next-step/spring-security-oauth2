package nextstep.app.application.google;

import nextstep.app.application.OAuth2Provider;
import nextstep.security.authentication.OAuth2AuthenticationRequestStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@OAuth2Provider("google")
public class GoogleAuthenticationRequestStrategy implements OAuth2AuthenticationRequestStrategy {
    private static final String SCOPE = "email profile";
    
    @Value("${oauth2.google.client-id}")
    private String clientId;
    @Value("${oauth2.google.authorization.request-uri}")
    private String baseRequestUri;
    @Value("${oauth2.google.authorization.redirect-uri}")
    private String redirectUri;

    @Override
    public String getOAuth2Type() {
        return "google";
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
