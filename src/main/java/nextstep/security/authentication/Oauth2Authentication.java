package nextstep.security.authentication;

import java.util.Set;

public class Oauth2Authentication implements Authentication {

    private final String id;
    private final Set<String> authorities = Set.of();
    private boolean authenticated = false;

    public Oauth2Authentication(String id) {
        this.id = id;
        this.authenticated = true;
    }

    @Override
    public Object getPrincipal() {
        return id;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Set<String> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }
}
