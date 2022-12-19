package nextstep.security.authentication;

import java.util.Set;

public class Oauth2Authentication implements Authentication {

    private final String id;
    private final Set<String> authorities;
    private final boolean authenticated;

    public Oauth2Authentication(
        String id,
        Set<String> authorities,
        boolean authenticated
    ) {
        this.id = id;
        this.authorities = authorities;
        this.authenticated = authenticated;
    }

    public static Oauth2Authentication ofRequest(String accessCode) {
        return new Oauth2Authentication(accessCode, Set.of(), false);
    }

    public static Authentication ofAuthenticated(String name, Set<String> authorities) {
        return new Oauth2Authentication(name, Set.of(), true);
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
