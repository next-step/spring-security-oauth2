package nextstep.security.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Oauth2AccessTokenRequest {
    @JsonProperty("client_id")
    private final String clientId;

    @JsonProperty("client_secret")
    private final String clientSecret;

    private final String code;

    @JsonProperty("redirect_uri")
    private final String redirectUri;

    @JsonProperty("grant_type")
    private final String grantType = "authorization_code";

    public Oauth2AccessTokenRequest(String code, String clientId, String clientSecret, String redirectUri) {
        this.code = code;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }
}
