package nextstep.oauth;

import nextstep.oauth.user.OAuth2User;
import nextstep.security.authentication.Authentication;

import java.util.Set;

public class OAuth2AuthenticationToken implements Authentication {
    private final OAuth2User principal;

    private final Set<String> authorities;

    private final boolean authenticated;

    public OAuth2AuthenticationToken(OAuth2User principal) {
        this.principal = principal;
        this.authorities = principal.getAuthorities();
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
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }
}
