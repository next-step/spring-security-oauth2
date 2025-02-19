package nextstep.app.application.github;

import nextstep.app.application.github.dto.GithubTokenRequest;
import nextstep.app.application.github.dto.GithubTokenResponse;
import nextstep.app.domain.ClientRegistration;
import nextstep.security.authentication.TokenRequest;
import nextstep.security.authentication.TokenResponse;
import nextstep.security.authentication.oauth.OAuth2TokenRequestStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GithubTokenRequestStrategy implements OAuth2TokenRequestStrategy {

    @Value("${oauth2.github.token.request-uri}")
    private String requestUri;

    @Override
    public String getRegistrationId() {
        return "github";
    }

    @Override
    public TokenRequest requestToken(ClientRegistration clientRegistration, String code) {
        return GithubTokenRequest.of(
                clientRegistration.clientId(), clientRegistration.clientSecret(), code, clientRegistration.redirectUri()
        );
    }

    @Override
    public String getRequestUri() {
        return requestUri;
    }

    @Override
    public Class<? extends TokenResponse> getResponseClass() {
        return GithubTokenResponse.class;
    }
}
