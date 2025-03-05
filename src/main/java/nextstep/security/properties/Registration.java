package nextstep.security.properties;

public class Registration {
    private String clientId;
    private String clientSecret;
    private String responseType;
    private String scope;
    private String authorizeUri;
    private String tokenUri;

    private String userUri;
    private String redirectUri;
    private String registrationId;


    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getResponseType() {
        return responseType;
    }

    public String getScope() {
        return scope;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public void setTokenUri(final String tokenUri) {
        this.tokenUri = tokenUri;
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(final String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setResponseType(final String responseType) {
        this.responseType = responseType;
    }

    public void setScope(final String scope) {
        this.scope = scope;
    }

    public String getAuthorizeUri() {
        return authorizeUri;
    }

    public void setAuthorizeUri(final String authorizeUri) {
        this.authorizeUri = authorizeUri;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(final String registrationId) {
        this.registrationId = registrationId;
    }

    public String getRedirectUri() {
        return this.redirectUri;
    }

    public void setRedirectUri(final String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getUserUri() {
        return userUri;
    }

    public void setUserUri(final String userUri) {
        this.userUri = userUri;
    }

}
