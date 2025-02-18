package nextstep.app.application.google.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.security.authentication.TokenRequest;

public record GoogleTokenRequest(
        String code,
        @JsonProperty("client_id")
        String clientId,
        @JsonProperty("client_secret")
        String clientSecret,
        @JsonProperty("redirect_uri")
        String redirectUri,
        @JsonProperty("grant_type")
        String grantType
) implements TokenRequest {
    public static GoogleTokenRequest of(String code, String clientId, String clientSecret, String redirectUri) {
        return new GoogleTokenRequest(
                code,
                clientId,
                clientSecret,
                redirectUri,
                "authorization_code"
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
