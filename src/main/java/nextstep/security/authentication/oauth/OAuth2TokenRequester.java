package nextstep.security.authentication.oauth;

import nextstep.security.authentication.TokenResponse;

public interface OAuth2TokenRequester {
    TokenResponse request(OAuth2AuthorizationRequest authorizationRequest, String code);
}
