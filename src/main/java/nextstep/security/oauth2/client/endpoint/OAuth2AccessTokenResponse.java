package nextstep.security.oauth2.client.endpoint;

import nextstep.security.oauth2.core.OAuth2AccessToken;

public record OAuth2AccessTokenResponse(
        OAuth2AccessToken accessToken
) {

}
