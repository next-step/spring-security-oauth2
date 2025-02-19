package nextstep.security.authentication.oauth;

import nextstep.security.authentication.TokenRequest;
import nextstep.security.authentication.TokenResponse;

public interface OAuth2TokenRequestStrategy {
    String getOAuth2Type();
    TokenRequest requestToken(OAuth2AuthorizationRequest request, String code);
    String getRequestUri();
    Class<? extends TokenResponse> getResponseClass();
}
