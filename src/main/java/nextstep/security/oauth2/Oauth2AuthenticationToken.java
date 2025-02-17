package nextstep.security.oauth2;

import nextstep.security.authentication.Authentication;

import java.util.Set;

public class Oauth2AuthenticationToken implements Authentication {

    private final Object principal;
    private final Object accessToken;
    private final boolean authenticated;
    private final Set<String> authorities;

    private Oauth2AuthenticationToken(Object principal, Object accessToken, boolean authenticated, Set<String> authorities) {
        this.principal = principal;
        this.accessToken = accessToken;
        this.authenticated = authenticated;
        this.authorities = authorities;
    }

    public static Oauth2AuthenticationToken unauthenticated(String principal, String credentials) {
        return new Oauth2AuthenticationToken(principal, credentials, false, Set.of());
    }


    public static Oauth2AuthenticationToken authenticated(String principal, String credentials, Set<String> authorities) {
        return new Oauth2AuthenticationToken(principal, credentials, true, authorities);
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
