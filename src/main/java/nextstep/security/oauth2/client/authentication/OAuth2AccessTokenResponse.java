package nextstep.security.oauth2.client.authentication;

import nextstep.security.oauth2.core.OAuth2AccessToken;

public class OAuth2AccessTokenResponse {
    private OAuth2AccessToken accessToken;

    public OAuth2AccessTokenResponse() {
    }

    public OAuth2AccessTokenResponse(OAuth2AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public OAuth2AccessToken getAccessToken() {
        return accessToken;
    }
}

