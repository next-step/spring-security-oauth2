package nextstep.app.application.google.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.security.authentication.TokenResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public record GoogleTokenResponse(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("expires_in")
        int expiresIn,
        @JsonProperty("token_type")
        String tokenType,
        @JsonProperty("scope")
        String scopeRaw,
        @JsonProperty("refresh_token")
        String refreshToken
) implements TokenResponse {
    @Override
    public String getAccessToken() {
        return accessToken;
    }

    public Set<String> getScope() {
        if (scopeRaw == null || scopeRaw.isBlank()) {
            return Collections.emptySet();
        }
        return Arrays.stream(scopeRaw.split(" ")).collect(Collectors.toSet());
    }
}
