package nextstep.app.application.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.security.authentication.TokenResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public record GithubTokenResponse(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("scope")
        String scopeRaw,
        @JsonProperty("token_type")
        String tokenType
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
