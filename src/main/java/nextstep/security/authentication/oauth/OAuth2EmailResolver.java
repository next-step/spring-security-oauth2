package nextstep.security.authentication.oauth;

import nextstep.security.authentication.TokenResponse;

public interface OAuth2EmailResolver {
    String resolve(String registrationId, TokenResponse token);
}
