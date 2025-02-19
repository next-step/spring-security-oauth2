package nextstep.security.authentication.oauth;

import nextstep.security.authentication.UserResponse;

public interface OAuth2EmailResolveStrategy {
    String getOAuth2Type();
    Class<? extends UserResponse> getUserResponseClass();
    String getRequestUri();
}
