package nextstep.security.authentication;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OAuth2AuthorizationRequest {
    private String authorizationGrantType;
    private String clientId;
    private Set<String> scopes;
    private String state;
    private Map<String, Object> additionalParameters;
    private Map<String, Object> attributes;
    private String tokenUri;
    private String userInfoUri;
    private String redirectUri;
    private String loginRedirectUri;

    public OAuth2AuthorizationRequest(ClientRegistration clientRegistration) {
        this.authorizationGrantType = clientRegistration.getGrantType();
        this.clientId = clientRegistration.getClientId();
        this.scopes = new HashSet<>();
        this.tokenUri = clientRegistration.getTokenUri();
        this.userInfoUri = clientRegistration.getUserInfoUri();
        this.redirectUri = clientRegistration.getRedirectUri();
        this.loginRedirectUri = clientRegistration.getLoginRedirectUri();
    }

    public OAuth2AuthorizationRequest(String clientId) {
        this.clientId = clientId;
    }

    public static OAuth2AuthorizationRequest from(ClientRegistration clientRegistration) {
        return new OAuth2AuthorizationRequest(clientRegistration);
    }

    public String getLoginRedirectUri() {
        return loginRedirectUri;
    }

    public String getClientId() {
        return clientId;
    }
}
