package nextstep.security.oauth2;

import nextstep.security.authentication.Authentication;
import nextstep.security.oauth2.registration.ClientRegistration;
import nextstep.security.oauth2.user.Oauth2User;

import java.util.Set;

public class OAuth2LoginAuthenticationToken implements Authentication {
    private final String principal;
    private final OAuth2AccessToken accessToken;
    private final ClientRegistration clientRegistration;
    private final boolean authenticated;
    private final Set<String> authorities;

    public OAuth2LoginAuthenticationToken(String principal, OAuth2AccessToken accessToken, ClientRegistration clientRegistration, boolean authenticated, Set<String> authorities) {
        this.principal = principal;
        this.accessToken = accessToken;
        this.clientRegistration = clientRegistration;
        this.authenticated = authenticated;
        this.authorities = authorities;
    }

    public static OAuth2LoginAuthenticationToken unauthenticated(ClientRegistration clientRegistration, OAuth2AccessToken credentials) {
        return new OAuth2LoginAuthenticationToken(null, credentials, clientRegistration, false, Set.of());
    }

    public static OAuth2LoginAuthenticationToken authenticated(Oauth2User principal, ClientRegistration clientRegistration, OAuth2AccessToken credentials, Set<String> authorities) {
        return new OAuth2LoginAuthenticationToken(principal.getEmail(), credentials, clientRegistration, true, authorities);
    }

    @Override
    public Set<String> getAuthorities() {
        return authorities;
    }

    @Override
    public OAuth2AccessToken getCredentials() {
        return accessToken;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    public ClientRegistration getClientRegistration() {
        return clientRegistration;
    }

    public OAuth2AccessToken getAccessToken() {
        return accessToken;
    }
}
