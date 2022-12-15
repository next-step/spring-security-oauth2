package nextstep.security.oauth2;

import nextstep.security.authentication.Authentication;

import java.util.Collections;
import java.util.Set;

public class OAuth2Authentication implements Authentication {
    private final String loginId;
    private final String password;
    private final Set<String> authorities;
    private boolean authenticated = false;

    private OAuth2Authentication(String loginId, String password, Set<String> authorities) {
        this.loginId = loginId;
        this.password = password;
        this.authorities = authorities;
    }

    public static OAuth2Authentication ofRequest(String loginId) {
        OAuth2Authentication authentication = new OAuth2Authentication(loginId, null, Collections.emptySet());
        authentication.authenticated = true;
        return authentication;
    }

    @Override
    public Object getPrincipal() {
        return loginId;
    }

    @Override
    public Object getCredentials() {
        return password;
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
