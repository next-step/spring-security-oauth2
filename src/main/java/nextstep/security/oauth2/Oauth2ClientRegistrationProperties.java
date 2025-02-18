package nextstep.security.oauth2;

public class Oauth2ClientRegistrationProperties {
    private final String clientId;
    private final String responseType;
    private final String redirectUri;
    private final String scope;
    private final String loginUrl;

    public Oauth2ClientRegistrationProperties(String clientId, String responseType, String redirectUri, String scope, String loginUrl) {
        this.clientId = clientId;
        this.responseType = responseType;
        this.redirectUri = redirectUri;
        this.scope = scope;
        this.loginUrl = loginUrl;
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

    public String getLoginUrl() {
        return loginUrl;
    }
}
