package nextstep.oauth;

import nextstep.oauth.user.OAuth2User;
import nextstep.security.authentication.Authentication;

import java.util.Set;

public class OAuth2LoginAuthenticationToken implements Authentication {
    private ClientRegistration clientRegistration;
    private OAuth2AuthorizationResponse oAuth2AuthorizationResponse;
    private OAuth2User principal;
    private String accessToken;
    private boolean authenticated;

    public OAuth2LoginAuthenticationToken(ClientRegistration clientRegistration
            , OAuth2AuthorizationResponse oAuth2AuthorizationResponse) {
        this.clientRegistration = clientRegistration;
        this.oAuth2AuthorizationResponse = oAuth2AuthorizationResponse;
        this.authenticated = false;
    }

    public OAuth2LoginAuthenticationToken(ClientRegistration clientRegistration
            , OAuth2AuthorizationResponse oAuth2AuthorizationResponse
            , OAuth2User principal
            , String accessToken) {
        this.clientRegistration = clientRegistration;
        this.oAuth2AuthorizationResponse = oAuth2AuthorizationResponse;
        this.principal = principal;
        this.accessToken = accessToken;
        this.authenticated = true;
    }

    @Override
    public Set<String> getAuthorities() {
        return principal.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
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

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public void setPrincipal(OAuth2User oauth2User) {
        this.principal = oauth2User;
    }
}
