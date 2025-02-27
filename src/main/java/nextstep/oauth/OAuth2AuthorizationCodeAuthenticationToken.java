package nextstep.oauth;

import nextstep.security.authentication.Authentication;

import java.util.Set;

public class OAuth2AuthorizationCodeAuthenticationToken implements Authentication {
    private final ClientRegistration clientRegistration;
    private final OAuth2AuthorizationResponse oAuth2AuthorizationResponse;
    private final String accessToken;
    private final boolean authenticated;

    public OAuth2AuthorizationCodeAuthenticationToken(ClientRegistration clientRegistration, OAuth2AuthorizationResponse oAuth2AuthorizationResponse) {
        this.clientRegistration = clientRegistration;
        this.oAuth2AuthorizationResponse = oAuth2AuthorizationResponse;
        this.accessToken = null;
        this.authenticated = false;
    }

    public OAuth2AuthorizationCodeAuthenticationToken(ClientRegistration clientRegistration
            , OAuth2AuthorizationResponse oAuth2AuthorizationResponse
            , String accessToken) {
        this.clientRegistration = clientRegistration;
        this.oAuth2AuthorizationResponse = oAuth2AuthorizationResponse;
        this.accessToken = accessToken;
        this.authenticated = true;
    }

    @Override
    public Set<String> getAuthorities() {
        return Set.of();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    public ClientRegistration getClientRegistration() {
        return clientRegistration;
    }

    public OAuth2AuthorizationResponse getoAuth2AuthorizationResponse() {
        return oAuth2AuthorizationResponse;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
