package nextstep.security.oauth2.authentication;

import nextstep.security.authentication.Authentication;
import nextstep.security.oauth2.client.ClientRegistration;
import nextstep.security.oauth2.endpoint.OAuth2AccessToken;
import nextstep.security.oauth2.exchange.OAuth2AuthorizationExchange;
import nextstep.security.oauth2.user.OAuth2User;

import java.util.Set;

public class OAuth2LoginAuthenticationToken implements Authentication {

    private final OAuth2User principal;
    private final ClientRegistration clientRegistration;
    private final OAuth2AuthorizationExchange authorizationExchange;
    private final OAuth2AccessToken accessToken;
    private final boolean authenticated;
    private final Set<String> authorities;

    public static OAuth2LoginAuthenticationToken authenticated(OAuth2User principal,
                                                               ClientRegistration clientRegistration,
                                                               OAuth2AuthorizationExchange authorizationExchange,
                                                               OAuth2AccessToken accessToken) {
        return new OAuth2LoginAuthenticationToken(principal, clientRegistration, authorizationExchange,
                                                  accessToken, true, accessToken.getScopes());
    }

    public OAuth2LoginAuthenticationToken(ClientRegistration clientRegistration,
                                          OAuth2AuthorizationExchange authorizationExchange) {

        this(null, clientRegistration, authorizationExchange, null, false, Set.of());
    }


    public OAuth2LoginAuthenticationToken(OAuth2User principal,
                                          ClientRegistration clientRegistration,
                                          OAuth2AuthorizationExchange authorizationExchange,
                                          OAuth2AccessToken accessToken,
                                          boolean authenticated,
                                          Set<String> authorities) {

        this.principal = principal;
        this.clientRegistration = clientRegistration;
        this.authorizationExchange = authorizationExchange;
        this.accessToken = accessToken;
        this.authenticated = authenticated;
        this.authorities = authorities;
    }

    @Override
    public Set<String> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public OAuth2User getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public String getName() {
        return principal.getName();
    }

    public OAuth2AccessToken getAccessToken() {
        return accessToken;
    }

    public ClientRegistration getClientRegistration() {
        return clientRegistration;
    }

    public OAuth2AuthorizationExchange getAuthorizationExchange() {
        return authorizationExchange;
    }
}
