package nextstep.security.oauth2.client.authentication;


import java.util.Set;
import nextstep.security.authentication.Authentication;
import nextstep.security.oauth2.client.registration.ClientRegistration;
import nextstep.security.oauth2.core.OAuth2AccessToken;
import nextstep.security.oauth2.core.OAuth2AuthorizationExchange;

public class OAuth2AuthorizationCodeAuthenticationToken implements Authentication {
    private ClientRegistration clientRegistration;

    private OAuth2AuthorizationExchange authorizationExchange;

    private OAuth2AccessToken accessToken;

    public OAuth2AuthorizationCodeAuthenticationToken(ClientRegistration clientRegistration,
                                                      OAuth2AuthorizationExchange authorizationExchange) {
        this.clientRegistration = clientRegistration;
        this.authorizationExchange = authorizationExchange;
    }

    public OAuth2AuthorizationCodeAuthenticationToken(ClientRegistration clientRegistration,
                                                      OAuth2AuthorizationExchange authorizationExchange,
                                                      OAuth2AccessToken accessToken) {
        this.clientRegistration = clientRegistration;
        this.authorizationExchange = authorizationExchange;
        this.accessToken = accessToken;
    }

    public ClientRegistration getClientRegistration() {
        return clientRegistration;
    }

    public OAuth2AuthorizationExchange getAuthorizationExchange() {
        return authorizationExchange;
    }

    public OAuth2AccessToken getAccessToken() {
        return accessToken;
    }

    @Override
    public Set<String> getAuthorities() {
        return Set.of();
    }

    @Override
    public Object getCredentials() {
        return (this.accessToken != null) ? this.accessToken.tokenValue()
                : this.authorizationExchange.authorizationResponse().code();
    }

    @Override
    public Object getPrincipal() {
        return this.clientRegistration.clientId();
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }
}

