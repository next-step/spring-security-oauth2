package nextstep.security.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.security.oauth2.registration.ClientRegistration;

public class OAuth2AccessTokenRequest {

    @JsonProperty("client_id")
    private final String clientId;

    @JsonProperty("client_secret")
    private final String clientSecret;

    private final String code;

    @JsonProperty("redirect_uri")
    private final String redirectUri;

    @JsonProperty("grant_type")
    private final String grantType = "authorization_code";

    private OAuth2AccessTokenRequest(String code, String clientId, String clientSecret, String redirectUri) {
        this.code = code;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    public static OAuth2AccessTokenRequest of(ClientRegistration clientRegistration, String code) {
        return new OAuth2AccessTokenRequest(code, clientRegistration.getClientId(), clientRegistration.getClientSecret(), clientRegistration.getRedirectUri());
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

    public String getGrantType() {
        return grantType;
    }
}
