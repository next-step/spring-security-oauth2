package nextstep.oauth2.authentication.token;

import nextstep.oauth2.userinfo.OAuth2User;
import nextstep.security.authentication.Authentication;

import java.util.Set;

public final class OAuth2AuthenticationToken implements Authentication {
    private final OAuth2User principal;
    private final Set<String> authorities;

    public OAuth2AuthenticationToken(OAuth2User principal, Set<String> authorities) {
        this.principal = principal;
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
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }
}
