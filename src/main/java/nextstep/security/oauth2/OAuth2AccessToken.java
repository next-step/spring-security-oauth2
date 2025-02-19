package nextstep.security.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuth2AccessToken {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }
}
