package nextstep.app.application.google;

import nextstep.app.application.google.dto.GoogleTokenRequest;
import nextstep.app.application.google.dto.GoogleTokenResponse;
import nextstep.security.authentication.OAuth2TokenRequestStrategy;
import nextstep.security.authentication.TokenRequest;
import nextstep.security.authentication.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleTokenRequestStrategy implements OAuth2TokenRequestStrategy {

    @Value("${oauth2.google.client-id}")
    private String clientId;
    @Value("${oauth2.google.client-secret}")
    private String clientSecret;
    @Value("${oauth2.google.token.redirect-uri}")
    private String redirectUri;
    @Value("${oauth2.google.token.request-uri}")
    private String requestUri;

    @Override
    public String getOAuth2Type() {
        return "google";
    }

    @Override
    public TokenRequest requestToken(String code) {
        return GoogleTokenRequest.of(code, clientId, clientSecret, redirectUri);
    }

    @Override
    public String getRequestUri() {
        return requestUri;
    }

    @Override
    public Class<? extends TokenResponse> getResponseClass() {
        return GoogleTokenResponse.class;
    }
}
