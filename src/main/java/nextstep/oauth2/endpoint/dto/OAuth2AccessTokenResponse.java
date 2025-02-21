package nextstep.oauth2.endpoint.dto;

import nextstep.oauth2.authentication.OAuth2AccessToken;

public record OAuth2AccessTokenResponse(
        OAuth2AccessToken accessToken
) {}
