package nextstep.app.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessRequest {
    @JsonProperty("client_id")
    private String clientId = "7fc956935c0618c560da";
    @JsonProperty("client_secret")
    private String clientSecret = "a3fd00e8f3146bf81e2c5b2ea328ccb8d330cd45";
    private String code;
    @JsonProperty("redirect_uri")
    private String redirectUri;

    public AccessRequest(String code) {
        this.code = code;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getCode() {
        return code;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}
