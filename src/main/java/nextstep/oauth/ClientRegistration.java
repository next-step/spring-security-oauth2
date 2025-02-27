package nextstep.oauth;

import java.util.Set;

public class ClientRegistration {
    private String registrationId;
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private Set<String> scope;
    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;
    private String userNameAttributeName;

    public ClientRegistration(String registrationId, String clientId, String clientSecret, String redirectUri
            , Set<String> scope, String authorizationUri, String tokenUri, String userInfoUri, String userNameAttributeName) {
        this.registrationId = registrationId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.scope = scope;
        this.authorizationUri = authorizationUri;
        this.tokenUri = tokenUri;
        this.userInfoUri = userInfoUri;
        this.userNameAttributeName = userNameAttributeName;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public Set<String> getScope() {
        return scope;
    }

    public String getAuthorizationUri() {
        return authorizationUri;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public String getUserInfoUri() {
        return userInfoUri;
    }

    public String getUserNameAttributeName() {
        return userNameAttributeName;
    }

    public String getRegistrationId() {
        return registrationId;
    }
}
