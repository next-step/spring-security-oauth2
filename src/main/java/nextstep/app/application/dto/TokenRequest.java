package nextstep.app.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenRequest(
        String code,
        @JsonProperty("client_id")
        String clientId,
        @JsonProperty("client_secret")
        String clientSecret,
        @JsonProperty("redirect_uri")
        String redirectUri,
        @JsonProperty("grant_type")
        String grantType
) {
    public static TokenRequest of(String code, String clientId, String clientSecret, String redirectUri) {
        return new TokenRequest(
                code,
                clientId,
                clientSecret,
                redirectUri,
                "authorization_code"
        );
    }
}
