package nextstep.app.application.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.security.authentication.TokenRequest;

public record GithubTokenRequest(
        @JsonProperty("client_id")
        String clientId,
        @JsonProperty("client_secret")
        String clientSecret,
        String code,
        @JsonProperty("redirect_uri")
        String redirectUri
) implements TokenRequest {
    public static GithubTokenRequest of(String clientId, String clientSecret, String code, String redirectUri) {
        return new GithubTokenRequest(
                clientId,
                clientSecret,
                code,
                redirectUri
        );
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public String getRedirectUri() {
        return redirectUri;
    }
}
