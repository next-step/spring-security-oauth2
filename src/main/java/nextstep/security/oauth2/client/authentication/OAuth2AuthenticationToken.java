package nextstep.security.oauth2.client.authentication;

import java.util.Set;
import nextstep.security.authentication.Authentication;
import nextstep.security.oauth2.core.user.OAuth2User;
import org.springframework.util.Assert;

public class OAuth2AuthenticationToken implements Authentication {
    private final OAuth2User principal;

    private final Set<String> authorities;

    private final boolean authenticated;

    public OAuth2AuthenticationToken(OAuth2User principal, Set<String> authorities) {
        Assert.notNull(principal, "principal cannot be null");
        this.principal = principal;
        this.authorities = authorities;
        this.authenticated = true;
    }

    @Override
    public Set<String> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getName() {
        return principal.toString();
    }

}
