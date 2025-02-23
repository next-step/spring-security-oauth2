package nextstep.security.oauth2.registration;

public class OAuth2ClientRegistrationProperties {
    private final String clientId;
    private final String responseType;
    private final String redirectUri;
    private final String scope;
    private final String clientSecret;

    public OAuth2ClientRegistrationProperties(String clientId, String responseType, String redirectUri, String scope, String clientSecret) {
        this.clientId = clientId;
        this.responseType = responseType;
        this.redirectUri = redirectUri;
        this.scope = scope;
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public String getResponseType() {
        return responseType;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getScope() {
        return scope;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
