package nextstep.app.application.github;

import nextstep.app.application.github.dto.GithubTokenRequest;
import nextstep.app.application.github.dto.GithubTokenResponse;
import nextstep.security.authentication.oauth.OAuth2AuthorizationRequest;
import nextstep.security.authentication.oauth.OAuth2TokenRequestStrategy;
import nextstep.security.authentication.TokenRequest;
import nextstep.security.authentication.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GithubTokenRequestStrategy implements OAuth2TokenRequestStrategy {

    @Value("${oauth2.github.token.request-uri}")
    private String requestUri;

    @Override
    public String getOAuth2Type() {
        return "github";
    }

    @Override
    public TokenRequest requestToken(OAuth2AuthorizationRequest request, String code) {
        return GithubTokenRequest.of(
                request.clientId(), request.clientSecret(), code, request.redirectUri()
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
