package nextstep.app.application.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.security.authentication.TokenResponse;

public record GithubTokenResponse(
        @JsonProperty("access_token")
        String accessToken,
        String scope,
        @JsonProperty("token_type")
        String tokenType
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
