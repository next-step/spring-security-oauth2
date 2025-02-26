package nextstep.oauth2.authentication;

import nextstep.oauth2.userinfo.OAuth2User;
import nextstep.oauth2.endpoint.OAuth2AuthorizationExchange;
import nextstep.oauth2.registration.ClientRegistration;
import nextstep.security.authentication.Authentication;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Set;

public class OAuth2LoginAuthenticationToken implements Authentication {

    private OAuth2User principal;

    private ClientRegistration clientRegistration;

    private OAuth2AuthorizationExchange authorizationExchange;

    private OAuth2AccessToken accessToken;

    private Set<String> authorities;

    private boolean authenticated;

    public static OAuth2LoginAuthenticationToken unauthenticated(final ClientRegistration clientRegistration, final OAuth2AuthorizationExchange authorizationExchange) {
        Assert.notNull(clientRegistration, "clientRegistration cannot be null");
        Assert.notNull(authorizationExchange, "authorizationExchange cannot be null");
        return new OAuth2LoginAuthenticationToken(clientRegistration, authorizationExchange);
    }

    private OAuth2LoginAuthenticationToken(ClientRegistration clientRegistration,
                                          OAuth2AuthorizationExchange authorizationExchange) {
        this.authorities = Collections.emptySet();
        this.clientRegistration = clientRegistration;
        this.authorizationExchange = authorizationExchange;
        this.authenticated = false;
    }

    public static Authentication authenticated(final ClientRegistration clientRegistration, final OAuth2AuthorizationExchange authorizationExchange, final OAuth2User principal, final Set<String> authorities, final OAuth2AccessToken accessToken) {
        Assert.notNull(clientRegistration, "clientRegistration cannot be null");
        Assert.notNull(authorizationExchange, "authorizationExchange cannot be null");
        Assert.notNull(principal, "principal cannot be null");
        Assert.notNull(accessToken, "accessToken cannot be null");
        return new OAuth2LoginAuthenticationToken(clientRegistration, authorizationExchange, principal, authorities, accessToken);
    }

    private OAuth2LoginAuthenticationToken(ClientRegistration clientRegistration,
                                          OAuth2AuthorizationExchange authorizationExchange, OAuth2User principal,
                                          Set<String> authorities, OAuth2AccessToken accessToken) {
        this.authorities = authorities;
        this.clientRegistration = clientRegistration;
        this.authorizationExchange = authorizationExchange;
        this.principal = principal;
        this.accessToken = accessToken;
        this.authenticated = true;
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

    public ClientRegistration getClientRegistration() {
        return clientRegistration;
    }

    public OAuth2AuthorizationExchange getAuthorizationExchange() {
        return authorizationExchange;
    }

    public OAuth2AccessToken getAccessToken() {
        return accessToken;
    }
}
