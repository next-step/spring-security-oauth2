package nextstep.security.oauth2.registration;

public class OAuth2ClientProviderProperties {
    private final String tokenUri;

    private final String userInfoUri;

    private final String authorizationUri;

    public OAuth2ClientProviderProperties(String tokenUri, String userInfoUri, String authorizationUri) {
        this.tokenUri = tokenUri;
        this.userInfoUri = userInfoUri;
        this.authorizationUri = authorizationUri;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public String getUserInfoUri() {
        return userInfoUri;
    }

    public String getAuthorizationUri() {
        return authorizationUri;
    }
}
