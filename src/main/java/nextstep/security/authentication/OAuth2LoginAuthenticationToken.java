package nextstep.security.authentication;

import java.util.Set;

public class OAuth2LoginAuthenticationToken implements Authentication{

    private String principal;
    private ClientRegistration clientRegistration;
    private String accessToken;
    private String refreshToken;
    private boolean authenticated;
    private String code;

    public OAuth2LoginAuthenticationToken(ClientRegistration clientRegistration, String code) {
        this.clientRegistration = clientRegistration;
        this.code = code;
        this.authenticated = false;
    }

    public OAuth2LoginAuthenticationToken(String principal, ClientRegistration clientRegistration, String accessToken, boolean authenticated) {
        this.principal = principal;
        this.clientRegistration = clientRegistration;
        this.accessToken = accessToken;
        this.authenticated = true;
    }

    public static OAuth2LoginAuthenticationToken authenticated(String principal, ClientRegistration clientRegistration, String accessToken) {
        return new OAuth2LoginAuthenticationToken(principal, clientRegistration, accessToken, true);
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
        return authenticated;
    }


    public ClientRegistration getClientRegistration() {
        return clientRegistration;
    }

    public String getCode() {
        return code;
    }
}
