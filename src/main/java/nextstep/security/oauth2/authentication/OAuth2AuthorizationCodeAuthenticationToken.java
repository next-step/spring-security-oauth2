package nextstep.security.oauth2.authentication;

import nextstep.security.authentication.Authentication;
import nextstep.security.oauth2.client.ClientRegistration;
import nextstep.security.oauth2.endpoint.OAuth2AccessToken;
import nextstep.security.oauth2.exchange.OAuth2AuthorizationExchange;

import java.util.Set;

public class OAuth2AuthorizationCodeAuthenticationToken implements Authentication {

    private final ClientRegistration clientRegistration;
    private final OAuth2AuthorizationExchange authorizationExchange;
    private final OAuth2AccessToken accessToken;

    private final boolean authenticated;
    private final Set<String> authorities;

    public static OAuth2AuthorizationCodeAuthenticationToken unauthenticated(ClientRegistration clientRegistration,
                                                                             OAuth2AuthorizationExchange authorizationExchange) {

        return new OAuth2AuthorizationCodeAuthenticationToken(clientRegistration,
                                                              authorizationExchange,
                                                              null,
                                                              false,
                                                              Set.of());
    }

    public OAuth2AuthorizationCodeAuthenticationToken(ClientRegistration clientRegistration,
                                                      OAuth2AuthorizationExchange authorizationExchange,
                                                      OAuth2AccessToken accessToken,
                                                      boolean authenticated, Set<String> authorities) {
        this.clientRegistration = clientRegistration;
        this.authorizationExchange = authorizationExchange;
        this.accessToken = accessToken;
        this.authenticated = authenticated;
        this.authorities = authorities;
    }

    public static Authentication authenticated(ClientRegistration clientRegistration,
                                               OAuth2AuthorizationExchange authorizationExchange,
                                               OAuth2AccessToken accessToken) {

        return new OAuth2AuthorizationCodeAuthenticationToken(clientRegistration,
                                                              authorizationExchange,
                                                              accessToken,
                                                              true,
                                                              accessToken.getScopes());
    }

    public ClientRegistration getClientRegistration() {
        return clientRegistration;
    }

    public String getCode() {
        return getAuthorizationExchange().getAuthorizationResponse().getCode();
    }

    @Override
    public Set<String> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return accessToken.getAccessToken();
    }

    @Override
    public Object getPrincipal() {
        return clientRegistration.getClientId();
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public String getName() {
        return "";
    }

    public OAuth2AuthorizationExchange getAuthorizationExchange() {
        return authorizationExchange;
    }

    public OAuth2AccessToken getAccessToken() {
        return accessToken;
    }
}
