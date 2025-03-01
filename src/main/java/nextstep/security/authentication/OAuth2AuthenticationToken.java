package nextstep.security.authentication;

import nextstep.security.userdetails.UserDetails;

import java.util.Set;

public class OAuth2AuthenticationToken implements Authentication {

    private ClientRegistration clientRegistration;
    private UserDetails principal;
    private String accessToken;
    private String refreshToken;

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
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }
}
