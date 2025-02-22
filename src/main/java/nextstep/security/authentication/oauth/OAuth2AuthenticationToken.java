package nextstep.security.authentication.oauth;

import nextstep.security.authentication.Authentication;

import java.util.Set;

public class OAuth2AuthenticationToken implements Authentication {

    private final OAuth2User principal;
    private final boolean authenticated;
    private final Set<String> authorities;

    private OAuth2AuthenticationToken(OAuth2User principal, boolean authenticated, Set<String> authorities) {
        this.principal = principal;
        this.authenticated = authenticated;
        this.authorities = authorities;
    }

    public static OAuth2AuthenticationToken authenticated(OAuth2User auth2User) {
        if (auth2User == null) {
            throw new IllegalArgumentException("auth2User cannot be null");
        }
        return new OAuth2AuthenticationToken(auth2User, true, auth2User.authorities());
    }

    @Override
    public Set<String> getAuthorities() {
        return authorities;
    }

    @Override
    public String getCredentials() {
        return "";
    }

    @Override
    public OAuth2User getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }
}
