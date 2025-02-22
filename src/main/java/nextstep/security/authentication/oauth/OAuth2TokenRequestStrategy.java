package nextstep.security.authentication.oauth;

import nextstep.app.domain.ClientRegistration;
import nextstep.security.authentication.TokenRequest;
import nextstep.security.authentication.TokenResponse;

public interface OAuth2TokenRequestStrategy {
    String getRegistrationId();
    TokenRequest requestToken(ClientRegistration clientRegistration, String code);
    String getRequestUri();
    Class<? extends TokenResponse> getResponseClass();
}
