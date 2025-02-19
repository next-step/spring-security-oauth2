package nextstep.app.application.google;

import nextstep.app.application.google.dto.GoogleTokenRequest;
import nextstep.app.application.google.dto.GoogleTokenResponse;
import nextstep.app.domain.ClientRegistration;
import nextstep.security.authentication.TokenRequest;
import nextstep.security.authentication.TokenResponse;
import nextstep.security.authentication.oauth.OAuth2TokenRequestStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleTokenRequestStrategy implements OAuth2TokenRequestStrategy {

    @Value("${oauth2.google.token.request-uri}")
    private String requestUri;
    @Value("${oauth2.google.registration-id}")
    private String registrationId;

    @Override
    public String getRegistrationId() {
        return registrationId;
    }

    @Override
    public TokenRequest requestToken(ClientRegistration clientRegistration, String code) {
        return GoogleTokenRequest.of(
                code, clientRegistration.clientId(), clientRegistration.clientSecret(), clientRegistration.redirectUri()
        );
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
