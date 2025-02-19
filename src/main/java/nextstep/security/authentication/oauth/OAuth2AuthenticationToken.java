package nextstep.security.authentication.oauth;

import nextstep.security.authentication.Authentication;

import java.util.Set;

public class OAuth2AuthenticationToken implements Authentication {

    private final Object principal;
    private final Object credentials;
    private final boolean authenticated;
    private final Set<String> authorities;

    private OAuth2AuthenticationToken(Object principal, Object credentials, boolean authenticated, Set<String> authorities) {
        this.principal = principal;
        this.credentials = credentials;
        this.authenticated = authenticated;
        this.authorities = authorities;
    }

    public static OAuth2AuthenticationToken authenticated(String principal, String credentials, Set<String> authorities) {
        return new OAuth2AuthenticationToken(principal, credentials, true, authorities);
    }

    @Override
    public Set<String> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return credentials;
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
