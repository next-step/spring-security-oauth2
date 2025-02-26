package nextstep.oauth2.authentication;

import nextstep.oauth2.client.userinfo.OAuth2User;
import nextstep.security.authentication.Authentication;

import java.util.Set;

public class OAuth2AuthenticationToken implements Authentication {

    private final OAuth2User principal;

    private final Set<String> authorities;

    private boolean authenticated = false;

    private OAuth2AuthenticationToken(final OAuth2User principal, final Set<String> authorities, final boolean authenticated) {
        this.principal = principal;
        this.authorities = authorities;
        this.authenticated = authenticated;
    }

    public static OAuth2AuthenticationToken success(final OAuth2User principal, final Set<String> authorities) {
        return new OAuth2AuthenticationToken(principal, authorities, true);
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

    public String principalName() {
        return principal.attributes().get(principal.nameAttributeKey()).toString();
    }
}
