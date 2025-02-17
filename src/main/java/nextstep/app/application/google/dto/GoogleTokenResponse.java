package nextstep.app.application.google.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.security.authentication.TokenResponse;

public record GoogleTokenResponse(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("expires_in")
        int expiresIn,
        @JsonProperty("token_type")
        String tokenType,
        String scope,
        @JsonProperty("refresh_token")
        String refreshToken
) implements TokenResponse {
    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String getTokenType() {
        return tokenType;
    }
}
