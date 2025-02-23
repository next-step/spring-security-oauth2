package nextstep.security.oauth2;

import nextstep.security.authentication.Authentication;

import java.util.Set;

public class OAuth2LoginAuthenticationToken implements Authentication {
    private final String principal;
    private final OAuth2AccessToken accessToken;
    private final boolean authenticated;
    private final Set<String> authorities;

    public OAuth2LoginAuthenticationToken(String principal, OAuth2AccessToken accessToken, boolean authenticated, Set<String> authorities) {
        this.principal = principal;
        this.accessToken = accessToken;
        this.authenticated = authenticated;
        this.authorities = authorities;
    }

    public static OAuth2LoginAuthenticationToken unauthenticated(String principal, OAuth2AccessToken credentials) {
        return new OAuth2LoginAuthenticationToken(principal, credentials, false, Set.of());
    }


    public static OAuth2LoginAuthenticationToken authenticated(String principal, OAuth2AccessToken credentials, Set<String> authorities) {
        return new OAuth2LoginAuthenticationToken(principal, credentials, true, authorities);
    }

    @Override
    public Set<String> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
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
}
