package nextstep.security.authentication;

import nextstep.security.userdetails.UserDetails;

import java.util.Set;

public class OAuth2AuthenticationToken implements Authentication {

    private Object principal;

    public OAuth2AuthenticationToken(Object principal) {
        this.principal = principal;
    }

    @Override
    public Set<String> getAuthorities() {
        return Set.of();
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
